package TelnetVT;
import Terminals.*;

/**
 * Created by Franco.Liu on 2014/1/17.
 */
public abstract class CVT100Enum extends TerminalBase {

    private enum States
    {
        None(0),
        Ground(1),
        EscapeIntrmdt(2),
        Escape(3),
        CsiEntry(4),
        CsiIgnore(5),
        CsiParam(6),
        CsiIntrmdt(7),
        OscString(8),
        SosPmApcString(9),
        DcsEntry(10),
        DcsParam(11),
        DcsIntrmdt(12),
        DcsIgnore(13),
        DcsPassthrough(14),
        Anywhere(16);

        private final int value;
        private States(int intValue)
        {
            value = intValue;

        }
    }

    public interface VtParserImplement {

        void onEventUcParser(Object Sender, ParserEventArgs e);
    }

    public final class StatesVal
    {
        States _States;

        public StatesVal(States Status)
        {
            _States=Status;
        }
        public void Set(States Status)
        {
            _States=Status;
        }
        public States Get()
        {
            return _States;
        }
    }

    protected final class ParserEventArgs
    {
        //UC
        public Actions Action;
        public char CurChar;
        public String CurSequence;
        public uc_Params CurParams;

        public ParserEventArgs()
        {
        }

        public ParserEventArgs(Actions p1, char p2, String p3, uc_Params p4)
        {
            Action = p1;
            CurChar = p2;
            CurSequence = p3;
            CurParams = p4;
        }
    }
    protected class uc_Parser
    {
        public VtParserImplement UcParserEvent ;

        States State = States.Ground;
        char CurChar = '\0';
        String CurSequence = "";


        uc_CharEvents CharEvents = new uc_CharEvents();
        uc_StateChangeEvents StateChangeEvents = new uc_StateChangeEvents();
        uc_Params CurParams = new uc_Params();

        public uc_Parser()
        {

        }
        public final void processChar(char ch)
        {
            //in uc_Parser
            StatesVal NextState =new StatesVal(States.None) ;
            ActionsVal NextAction = new ActionsVal(Actions.None);
            ActionsVal StateExitAction = new ActionsVal(Actions.None);
            Actions StateEntryAction = Actions.None;
            this.CurChar = ch;

            // Get the next state and associated action based
            // on the current state and char event
            CharEvents.GetStateEventAction(State, CurChar, NextState, NextAction);

            // execute any actions arising from leaving the current state
            if (NextState.Get() != States.None && NextState.Get() != this.State)
            {
                // check for state exit actions

                StateChangeEvents.GetStateChangeAction(this.State, Transitions.Exit, StateExitAction);

                // Process the exit action
                if (StateExitAction.Get() != Actions.None)
                {
                    DoAction(StateExitAction.Get());
                }

            }

            // process the action specified
            if (NextAction.Get() != Actions.None)
            {
                DoAction(NextAction.Get());
            }

            // set the new parser state and execute any actions arising entering the new state
            if (NextState.Get() != States.None && NextState.Get() != this.State)
            {
                // change the parsers state attribute
                this.State = NextState.Get();

                // check for state entry actions

                StateChangeEvents.GetStateChangeAction(this.State, Transitions.Entry, StateExitAction);

                // Process the entry action
                if (StateEntryAction != Actions.None)
                {
                    DoAction(StateEntryAction);
                }
            }
        }
        private void DoAction(Actions NextAction)
        {
            // in UC
            // Manage the contents of the Sequence and Param Variables
            switch (NextAction)
            {
                case Dispatch:
                case Collect:
                    this.CurSequence += String.valueOf(CurChar);
                    break;

                case NewCollect:
                    this.CurSequence =  String.valueOf(CurChar);
                    this.CurParams.Clear();
                    break;

                case Param:
                    this.CurParams.Add(CurChar);
                    break;

                default:
                    break;
            }

            // send the external event requests
            switch (NextAction)
            {
                case Dispatch:
                case Execute:
                case Put:
                case OscStart:
                case OscPut:
                case OscEnd:
                case Unhook:
                case Print:

                    //                    System.Console.Write ("Sequence = {0}, Char = {1}, PrmCount = {2}, State = {3}, NextAction = {4}\n",
                    //                        this.CurSequence, this.CurChar.ToString (), this.CurParams.Count ().ToString (),
                    //                        this.State.ToString (), NextAction.ToString ());

                    UcParserEvent.onEventUcParser(this, new ParserEventArgs(NextAction, CurChar, CurSequence, CurParams));
                    break;

                default:
                    break;
            }


            switch (NextAction)
            {
                case Dispatch:
                    this.CurSequence = "";
                    this.CurParams.Clear();
                    break;
                default:
                    break;
            }
        }
        private final class uc_StateChangeInfo
        {
            // in UC
            public States State;
            public Transitions Transition; // the next state we are going to
            public Actions NextAction;

            public uc_StateChangeInfo(States p1, Transitions p2,Actions p3)
            {
                this.State = p1;
                this.Transition = p2;
                this.NextAction = p3;
            }
        }

        private class uc_StateChangeEvents
        {
            //  in UC
            private uc_StateChangeInfo[] Elements = {
                    new uc_StateChangeInfo(States.OscString, Transitions.Entry, Actions.OscStart),
                    new uc_StateChangeInfo(States.OscString, Transitions.Exit, Actions.OscEnd),
                    new uc_StateChangeInfo(States.DcsPassthrough, Transitions.Entry, Actions.Hook),
                    new uc_StateChangeInfo(States.DcsPassthrough, Transitions.Exit, Actions.Unhook)
            };

            public uc_StateChangeEvents()
            {
            }

            public final boolean GetStateChangeAction(States State, Transitions Transition, ActionsVal NextAction)
            {
                //thos is uc_Parser
                uc_StateChangeInfo Element;

                for (int i = 0; i < Elements.length; i++)
                {
                    Element = Elements[i];

                    if (State == Element.State && Transition == Element.Transition)
                    {
                        NextAction.Set(Element.NextAction);
                        return true;
                    }
                }

                return false;
            }
        }

        private final class uc_CharEventInfo
        {
            // in UC
            public States CurState;
            public char CharFrom;
            public char CharTo;
            public Actions NextAction;
            public States NextState; // the next state we are going to

            public uc_CharEventInfo(States p1, char p2, char p3, Actions p4, States p5)
            {
                this.CurState = p1;
                this.CharFrom = p2;
                this.CharTo = p3;
                this.NextAction = p4;
                this.NextState = p5;
            }
        }

        private class uc_CharEvents
        {
            // in UC
            public final boolean GetStateEventAction(States CurState, char CurChar, StatesVal NextState, ActionsVal NextAction)
            {
                uc_CharEventInfo Element;

                // Codes A0-FF are treated exactly the same way as 20-7F
                // so we can keep are state table smaller by converting before we look
                // up the event associated with the character

                if (CurChar >= '\u00A0' && CurChar <= '\u00FF')
                {
                    CurChar -= '\u0080';
                }

                for (int i = 0; i < Elements.length; i++)
                {
                    Element = Elements[i];

                    if (CurChar >= Element.CharFrom && CurChar <= Element.CharTo && (CurState == Element.CurState || Element.CurState == States.Anywhere))
                    {
                        NextState.Set(Element.NextState);
                        NextAction.Set(Element.NextAction);
                        return true;
                    }
                }

                return false;
            }

            public uc_CharEvents()
            {
            }

            //region uc_CharEventInfo[] Elements
            public uc_CharEventInfo[] Elements = {
                    new uc_CharEventInfo(States.Anywhere, '\u001b', '\u001b', Actions.NewCollect, States.Escape),
                    new uc_CharEventInfo(States.Anywhere, '\u0018', '\u0018', Actions.Execute, States.Ground),
                    new uc_CharEventInfo(States.Anywhere, '\u001A', '\u001A', Actions.Execute, States.Ground),
                    new uc_CharEventInfo(States.Anywhere, '\u001A', '\u001A', Actions.Execute, States.Ground),
                    new uc_CharEventInfo(States.Anywhere, '\u0080', '\u008F', Actions.Execute, States.Ground),
                    new uc_CharEventInfo(States.Anywhere, '\u0091', '\u0097', Actions.Execute, States.Ground),
                    new uc_CharEventInfo(States.Anywhere, '\u0099', '\u0099', Actions.Execute, States.Ground),
                    new uc_CharEventInfo(States.Anywhere, '\u009A', '\u009A', Actions.Execute, States.Ground),
                    new uc_CharEventInfo(States.Anywhere, '\u009C', '\u009C', Actions.Execute, States.Ground),
                    new uc_CharEventInfo(States.Anywhere, '\u0098', '\u0098', Actions.None, States.SosPmApcString),
                    new uc_CharEventInfo(States.Anywhere, '\u009E', '\u009F', Actions.None, States.SosPmApcString),
                    new uc_CharEventInfo(States.Anywhere, '\u0090', '\u0090', Actions.NewCollect, States.DcsEntry),
                    new uc_CharEventInfo(States.Anywhere, '\u009D', '\u009D', Actions.None, States.OscString),
                    new uc_CharEventInfo(States.Anywhere, '\u009B', '\u009B', Actions.NewCollect, States.CsiEntry),
                    new uc_CharEventInfo(States.Ground, '\u0000', '\u0017', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.Ground, '\u0000', '\u0017', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.Ground, '\u0019', '\u0019', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.Ground, '\u001C', '\u001F', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.Ground, '\u0020', '\u007F', Actions.Print, States.None),
                    new uc_CharEventInfo(States.Ground, '\u0080', '\u008F', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.Ground, '\u0091', '\u009A', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.Ground, '\u009C', '\u009C', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.EscapeIntrmdt, '\u0000', '\u0017', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.EscapeIntrmdt, '\u0019', '\u0019', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.EscapeIntrmdt, '\u001C', '\u001F', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.EscapeIntrmdt, '\u0020', '\u002F', Actions.Collect, States.None),
                    new uc_CharEventInfo(States.EscapeIntrmdt, '\u0030', '\u007E', Actions.Dispatch, States.Ground),
                    new uc_CharEventInfo(States.Escape, '\u0000', '\u0017', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.Escape, '\u0019', '\u0019', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.Escape, '\u001C', '\u001F', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.Escape, '\u0058', '\u0058', Actions.None, States.SosPmApcString),
                    new uc_CharEventInfo(States.Escape, '\u005E', '\u005F', Actions.None, States.SosPmApcString),
                    new uc_CharEventInfo(States.Escape, '\u0050', '\u0050', Actions.Collect, States.DcsEntry),
                    new uc_CharEventInfo(States.Escape, '\u005D', '\u005D', Actions.None, States.OscString),
                    new uc_CharEventInfo(States.Escape, '\u005B', '\u005B', Actions.Collect, States.CsiEntry),
                    new uc_CharEventInfo(States.Escape, '\u0030', '\u004F', Actions.Dispatch, States.Ground),
                    new uc_CharEventInfo(States.Escape, '\u0051', '\u0057', Actions.Dispatch, States.Ground),
                    new uc_CharEventInfo(States.Escape, (char)92,(char)92, Actions.Dispatch, States.Ground),//  \
                    new uc_CharEventInfo(States.Escape, '\u0059', '\u005A', Actions.Dispatch, States.Ground),

                    new uc_CharEventInfo(States.Escape, '\u0060', '\u007E', Actions.Dispatch, States.Ground),
                    new uc_CharEventInfo(States.Escape, '\u0020', '\u002F', Actions.Collect, States.EscapeIntrmdt),
                    new uc_CharEventInfo(States.CsiEntry, '\u0000', '\u0017', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiEntry, '\u0019', '\u0019', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiEntry, '\u001C', '\u001F', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiEntry, '\u0020', '\u002F', Actions.Collect, States.CsiIntrmdt),
                    new uc_CharEventInfo(States.CsiEntry, '\u003A', '\u003A', Actions.None, States.CsiIgnore),
                    new uc_CharEventInfo(States.CsiEntry, '\u003C', '\u003F', Actions.Collect, States.CsiParam),
                    new uc_CharEventInfo(States.CsiEntry, '\u003C', '\u003F', Actions.Collect, States.CsiParam),
                    new uc_CharEventInfo(States.CsiEntry, '\u0030', '\u0039', Actions.Param, States.CsiParam),
                    new uc_CharEventInfo(States.CsiEntry, '\u003B', '\u003B', Actions.Param, States.CsiParam),
                    new uc_CharEventInfo(States.CsiEntry, '\u003C', '\u003F', Actions.Collect, States.CsiParam),
                    new uc_CharEventInfo(States.CsiEntry, '\u0040', '\u007E', Actions.Dispatch, States.Ground),
                    new uc_CharEventInfo(States.CsiParam, '\u0000', '\u0017', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiParam, '\u0019', '\u0019', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiParam, '\u001C', '\u001F', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiParam, '\u0030', '\u0039', Actions.Param, States.None),
                    new uc_CharEventInfo(States.CsiParam, '\u003B', '\u003B', Actions.Param, States.None),
                    new uc_CharEventInfo(States.CsiParam, '\u003A', '\u003A', Actions.None, States.CsiIgnore),
                    new uc_CharEventInfo(States.CsiParam, '\u003C', '\u003F', Actions.None, States.CsiIgnore),
                    new uc_CharEventInfo(States.CsiParam, '\u0020', '\u002F', Actions.Collect, States.CsiIntrmdt),
                    new uc_CharEventInfo(States.CsiParam, '\u0040', '\u007E', Actions.Dispatch, States.Ground),
                    new uc_CharEventInfo(States.CsiIgnore, '\u0000', '\u0017', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiIgnore, '\u0019', '\u0019', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiIgnore, '\u001C', '\u001F', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiIgnore, '\u0040', '\u007E', Actions.None, States.Ground),
                    new uc_CharEventInfo(States.CsiIntrmdt, '\u0000', '\u0017', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiIntrmdt, '\u0019', '\u0019', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiIntrmdt, '\u001C', '\u001F', Actions.Execute, States.None),
                    new uc_CharEventInfo(States.CsiIntrmdt, '\u0020', '\u002F', Actions.Collect, States.None),
                    new uc_CharEventInfo(States.CsiIntrmdt, '\u0030', '\u003F', Actions.None, States.CsiIgnore),
                    new uc_CharEventInfo(States.CsiIntrmdt, '\u0040', '\u007E', Actions.Dispatch, States.Ground),
                    new uc_CharEventInfo(States.SosPmApcString,'\u009C', '\u009C', Actions.None, States.Ground),
                    new uc_CharEventInfo(States.DcsEntry, '\u0020', '\u002F', Actions.Collect, States.DcsIntrmdt),
                    new uc_CharEventInfo(States.DcsEntry, '\u003A', '\u003A', Actions.None, States.DcsIgnore),
                    new uc_CharEventInfo(States.DcsEntry, '\u0030', '\u0039', Actions.Param, States.DcsParam),
                    new uc_CharEventInfo(States.DcsEntry, '\u003B', '\u003B', Actions.Param, States.DcsParam),
                    new uc_CharEventInfo(States.DcsEntry, '\u003C', '\u003F', Actions.Collect, States.DcsParam),
                    new uc_CharEventInfo(States.DcsEntry, '\u0040', '\u007E', Actions.None, States.DcsPassthrough),
                    new uc_CharEventInfo(States.DcsIntrmdt, '\u0030', '\u003F', Actions.None, States.DcsIgnore),
                    new uc_CharEventInfo(States.DcsIntrmdt, '\u0040', '\u007E', Actions.None, States.DcsPassthrough),
                    new uc_CharEventInfo(States.DcsIgnore, '\u009C', '\u009C', Actions.None, States.Ground),
                    new uc_CharEventInfo(States.DcsParam, '\u0030', '\u0039', Actions.Param, States.None),
                    new uc_CharEventInfo(States.DcsParam, '\u003B', '\u003B', Actions.Param, States.None),
                    new uc_CharEventInfo(States.DcsParam, '\u0020', '\u002F', Actions.Collect, States.DcsIntrmdt),
                    new uc_CharEventInfo(States.DcsParam, '\u003A', '\u003A', Actions.None, States.DcsIgnore),
                    new uc_CharEventInfo(States.DcsParam, '\u003C', '\u003F', Actions.None, States.DcsIgnore),
                    new uc_CharEventInfo(States.DcsPassthrough,'\u0000', '\u0017', Actions.Put, States.None),
                    new uc_CharEventInfo(States.DcsPassthrough,'\u0019', '\u0019', Actions.Put, States.None),
                    new uc_CharEventInfo(States.DcsPassthrough,'\u001C', '\u001F', Actions.Put, States.None),
                    new uc_CharEventInfo(States.DcsPassthrough,'\u0020', '\u007E', Actions.Put, States.None),
                    new uc_CharEventInfo(States.DcsPassthrough,'\u009C', '\u009C', Actions.None, States.Ground),
                    new uc_CharEventInfo(States.OscString, '\u0020', '\u007F', Actions.OscPut, States.None),
                    new uc_CharEventInfo(States.OscString, '\u009C', '\u009C', Actions.None, States.Ground)
            };

            //endregion
        }
    }

}

package com.te.UI;

import Terminals.CipherConnectSettingInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.example.terminalemulation.R;
 
public class LeftMenuFrg extends Fragment {

    public interface LeftMenuListener {
        public void onDrawerItemSelected(int position);
        public void onDrawerItemDelete(int position);
        public void onDrawerItemSetting(int position);
        public void onAddSession();
        public void onAbout();
        public void onExit();
    }

    private RelativeLayout mAddSession = null;
    private SessionsView mSessionsView = null;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    
    private LeftMenuListener mLeftMenuListener;
 
    public LeftMenuFrg() {
 
    }
 
    public void setDrawerListener(LeftMenuListener listener) {
        mLeftMenuListener = listener;
    }
    
    public String getItemTitle(int nPos) {
    	return mSessionsView.getSessionTitle(nPos);
    }
    
    public void setUp(Toolbar toolbar) {
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.mainActivityDrawer);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }
 
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
 
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
               
            }
        };
 
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
 
        // preparing session items
        syncSessionsViewFromSettings();
        mSessionsView.setSelected(CipherConnectSettingInfo.GetSessionIndex());
    }

    void syncSessionsViewFromSettings() {
        mSessionsView.removeAllSessions();
        for (int idxSession = 0; idxSession < CipherConnectSettingInfo.GetSessionCount(); ++idxSession) {
            String strTitle =
                    String.format(getResources().getString(R.string.Format_Session),
                            idxSession,
                            CipherConnectSettingInfo.getHostAddrByIndex(idxSession));
            mSessionsView.addSession(strTitle);
        }
        mSessionsView.addSession(getResources().getString(R.string.about));
        mSessionsView.addSession(getResources().getString(R.string.exit));
        mSessionsView.refresh();
    }

    public void clickSession(int position) {
        mLeftMenuListener.onDrawerItemSelected(position);
        mDrawerLayout.closeDrawer(LeftMenuFrg.this.getView());
        mSessionsView.setSelected(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View leftMenuView = inflater.inflate(R.layout.left_menu_fragment, container, false);
        mAddSession = (RelativeLayout) leftMenuView.findViewById(R.id.add_session);
        mAddSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftMenuListener.onAddSession();
            }
        });

        mSessionsView = (SessionsView) leftMenuView.findViewById(R.id.drawerList);
        mSessionsView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickSession(position);
                mSessionsView.closeAllSessionDeleteView();
            }
        });

        mSessionsView.setOnItemClickPartListener(new SessionsView.OnItemClickPartListener() {
            @Override
            public void onItemClickDelete(int pos) {
                if(CipherConnectSettingInfo.GetSessionCount() <= 1) {
                    Toast.makeText(getContext(), R.string.MSG_DeleteSession_Warn, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pos == CipherConnectSettingInfo.GetSessionIndex()) {
                    if(pos == 0) { //choose next one
                        int nNextPos = CipherConnectSettingInfo.GetSessionIndex() + 1;
                        clickSession(nNextPos); //select previous item
                        mSessionsView.removeSession(pos);
                        CipherConnectSettingInfo.removeSession(pos);
                        mSessionsView.refresh();
                        mSessionsView.setSelected(nNextPos - 1);
                        mLeftMenuListener.onDrawerItemDelete(pos);
                    }
                    else { //choose previous
                        int nPreviousPos = CipherConnectSettingInfo.GetSessionIndex() - 1;
                        clickSession(nPreviousPos); //select previous item
                        mSessionsView.removeSession(pos);
                        CipherConnectSettingInfo.removeSession(pos);
                        mSessionsView.refresh();
                        mSessionsView.setSelected(nPreviousPos);
                        mLeftMenuListener.onDrawerItemDelete(pos);
                    }
                } else {    //pos > CipherConnectSettingInfo.GetSessionIndex() || //pos < CipherConnectSettingInfo.GetSessionIndex()
                    mSessionsView.removeSession(pos);
                    CipherConnectSettingInfo.removeSession(pos);
                    mSessionsView.refresh();
                    mSessionsView.setSelected(CipherConnectSettingInfo.GetSessionIndex());
                    mLeftMenuListener.onDrawerItemDelete(pos);
                }
            }

            @Override
            public void onItemClickSetting(int pos) {
                mLeftMenuListener.onDrawerItemSetting(pos);
            }

            @Override
            public void onItemClickAbout() {
                mLeftMenuListener.onAbout();
            }

            @Override
            public void onItemClickExit() {
                mLeftMenuListener.onExit();
            }
        });
        return leftMenuView;
    }
}
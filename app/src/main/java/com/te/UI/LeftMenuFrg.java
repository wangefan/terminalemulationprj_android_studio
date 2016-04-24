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

import com.example.terminalemulation.R;
 
public class LeftMenuFrg extends Fragment {

    public interface LeftMenuListener {
        public void onDrawerItemSelected(int position);
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
        for (int idxSession = 0; idxSession < CipherConnectSettingInfo.GetSessionCount(); ++idxSession) {
            String strTitle = 
                    String.format(getResources().getString(R.string.Format_Session),
                                  idxSession,
                                  CipherConnectSettingInfo.getHostAddrByIndex(idxSession));
            mSessionsView.addSession(strTitle);
        }
        mSessionsView.setSelected(CipherConnectSettingInfo.GetSessionIndex());
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
                mLeftMenuListener.onDrawerItemSelected(position);
                mDrawerLayout.closeDrawer(LeftMenuFrg.this.getView());
            }
        });
        
        LinearLayout layAbout = (LinearLayout) leftMenuView.findViewById(R.id.about);
        layAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftMenuListener.onAbout();
            }
        });
        
        LinearLayout layExit = (LinearLayout) leftMenuView.findViewById(R.id.exit);
        layExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftMenuListener.onExit();
            }
        });
 
        return leftMenuView;
    }
}
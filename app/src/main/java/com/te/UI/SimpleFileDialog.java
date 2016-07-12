package com.te.UI;// SimpleFileDialog.java


/*
* 
* This file is licensed under The Code Project Open License (CPOL) 1.02 
* http://www.codeproject.com/info/cpol10.aspx
* http://www.codeproject.com/info/CPOL.zip
* 
* License Preamble:
* This License governs Your use of the Work. This License is intended to allow developers to use the Source
* Code and Executable Files provided as part of the Work in any application in any form.
* 
* The main points subject to the terms of the License are:
*    Source Code and Executable Files can be used in commercial applications;
*    Source Code and Executable Files can be redistributed; and
*    Source Code can be modified to create derivative works.
*    No claim of suitability, guarantee, or any warranty whatsoever is provided. The software is provided "as-is".
*    The Article(s) accompanying the Work may not be distributed or republished without the Author's consent
* 
* This License is entered between You, the individual or other entity reading or otherwise making use of
* the Work licensed pursuant to this License and the individual or other entity which offers the Work
* under the terms of this License ("Author").
*  (See Links above for full license text)
*/

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.terminalemulation.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimpleFileDialog {
    public enum Type {
        FILE_OPEN,
        FILE_SAVE,
        FOLDER_CHOOSE
    }

    private Type mSelectType = Type.FILE_SAVE;
    private String m_sdcardDirectory = "";
    private String mTitle = "";
    private Context m_context;
    private TextView m_tvCurrentPath;
    private TextView mtvFileInfoTitle;
    private TextView mtvFileInfo;

    private String m_dir = "";
    private List<String> m_subdirs = null;
    private SimpleFileDialogListener m_SimpleFileDialogListener = null;
    private ArrayAdapter<String> m_listAdapter = null;

    public SimpleFileDialog(Context context, String title, Type file_select_type, SimpleFileDialogListener SimpleFileDialogListener) {
        mSelectType = file_select_type;
        mTitle = title;
        m_context = context;
        m_sdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        m_SimpleFileDialogListener = SimpleFileDialogListener;

        try {
            m_sdcardDirectory = new File(m_sdcardDirectory).getCanonicalPath();
        } catch (IOException ioe) {
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // chooseFile_or_Dir(String dir) - load directory chooser dialog for initial
    // input 'dir' directory
    ////////////////////////////////////////////////////////////////////////////////
    public void chooseFile_or_Dir(String dir) {
        File dirFile = new File(dir);
        String curFile = "";
        if (!dirFile.exists()) {
            dir = m_sdcardDirectory;
        } else if(!dirFile.isDirectory()) {
            try {
                curFile = dirFile.getName();
                dir = dirFile.getCanonicalPath().replaceAll(dirFile.getName(), "");
            } catch (IOException e) {
                e.printStackTrace();
            }
            ;
        } else {
            try {
                dir = new File(dir).getCanonicalPath();
            } catch (IOException ioe) {
                return;
            }
        }

        m_dir = dir;
        m_subdirs = getDirectories(dir);

        AlertDialog.Builder dialogBuilder = createDirectoryChooserDialog(curFile, dir, m_subdirs,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String m_dir_old = m_dir;
                        String sel = "" + parent.getItemAtPosition(position);
                        if (sel.charAt(sel.length() - 1) == '/') sel = sel.substring(0, sel.length() - 1);

                        // Navigate into the sub-directory
                        if (sel.equals("..")) {
                            m_dir = m_dir.substring(0, m_dir.lastIndexOf("/"));
                        } else {
                            m_dir += "/" + sel;
                        }

                        if ((new File(m_dir).isFile())) { // If the selection is a regular file
                            m_dir = m_dir_old;
                            mtvFileInfo.setText(sel);
                        }

                        updateDirectory();
                    }
                });

        dialogBuilder.setPositiveButton(R.string.STR_OK, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Current directory chosen
                // Call registered listener supplied with the chosen directory
                if (m_SimpleFileDialogListener != null) {
                    if (mSelectType == Type.FILE_OPEN || mSelectType == Type.FILE_SAVE) {
                        m_SimpleFileDialogListener.onChosenDir(m_dir + "/" + mtvFileInfo.getText());
                    } else {
                        m_SimpleFileDialogListener.onChosenDir(m_dir);
                    }
                }
            }
        }).setNegativeButton(R.string.STR_Cancel, null);

        final AlertDialog dirsDialog = dialogBuilder.create();

        // Show directory chooser dialog
        dirsDialog.show();
    }

    private boolean createSubDir(String newDir) {
        File newDirFile = new File(newDir);
        if (!newDirFile.exists()) return newDirFile.mkdir();
        else return false;
    }

    private List<String> getDirectories(String dir) {
        List<String> dirs = new ArrayList<String>();
        try {
            File dirFile = new File(dir);

            // if directory is not the base sd card directory add ".." for going up one directory
            if (!m_dir.equals(m_sdcardDirectory)) dirs.add("..");

            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return dirs;
            }

            for (File file : dirFile.listFiles()) {
                if (file.isDirectory()) {
                    // Add "/" to directory names to identify them in the list
                    dirs.add(file.getName() + "/");
                } else if (mSelectType == Type.FILE_SAVE || mSelectType == Type.FILE_OPEN) {
                    // Add file names to the list if we are doing a file save or file open operation
                    String fullPath = file.getCanonicalPath();
                    int dot = fullPath.lastIndexOf(".wav");
                    if(dot >=0) {
                        String extension = fullPath.substring(dot + 1);
                        if(extension.compareToIgnoreCase("wav") == 0) {
                            dirs.add(file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        Collections.sort(dirs, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return dirs;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////                                   START DIALOG DEFINITION                                    //////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private AlertDialog.Builder createDirectoryChooserDialog(String curFile, String curPath, List<String> listItems,
                                                             OnItemClickListener onClickListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(m_context);

        // Create custom view for AlertDialog curPath
        LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(m_context).inflate(R.layout.simple_file_chooser, null);

        m_tvCurrentPath = (TextView) dialogLayout.findViewById(R.id.dialog_cur_path);
        m_tvCurrentPath.setText(curPath);
        mtvFileInfoTitle = (TextView) dialogLayout.findViewById(R.id.info_title);
        mtvFileInfo = (TextView) dialogLayout.findViewById(R.id.file_name_info);
        if (mSelectType == Type.FILE_OPEN) {
            mtvFileInfoTitle.setText(R.string.str_info_title);
            mtvFileInfo.setText(curFile);
        }
        if (mSelectType == Type.FOLDER_CHOOSE || mSelectType == Type.FILE_SAVE) {
            ///////////////////////////////
            // Create New Folder Button  //
            ///////////////////////////////
            Button newDirButton = new Button(m_context);
            newDirButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            newDirButton.setText("New Folder");
            newDirButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    final EditText input = new EditText(m_context);

                                                    // Show new folder name input dialog
                                                    new AlertDialog.Builder(m_context).
                                                            setTitle("New Folder Name").
                                                            setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            Editable newDir = input.getText();
                                                            String newDirName = newDir.toString();
                                                            // Create new directory
                                                            if (createSubDir(m_dir + "/" + newDirName)) {
                                                                // Navigate into the new directory
                                                                m_dir += "/" + newDirName;
                                                                updateDirectory();
                                                            } else {
                                                                Toast.makeText(m_context, "Failed to create '"
                                                                        + newDirName + "' folder", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }).setNegativeButton("Cancel", null).show();
                                                }
                                            }
            );
            dialogLayout.addView(newDirButton);
        }

        dialogBuilder.setTitle(mTitle);
        dialogBuilder.setView(dialogLayout);
        m_listAdapter = createListAdapter(listItems);
        ListView list = (ListView) dialogLayout.findViewById(R.id.dialoglist);
        list.setOnItemClickListener(onClickListener);
        list.setAdapter(m_listAdapter);
        dialogBuilder.setCancelable(false);
        return dialogBuilder;
    }

    private void updateDirectory() {
        m_subdirs.clear();
        m_subdirs.addAll(getDirectories(m_dir));
        m_tvCurrentPath.setText(m_dir);
        m_listAdapter.notifyDataSetChanged();
    }

    private ArrayAdapter<String> createListAdapter(List<String> items) {
        return new ArrayAdapter<String>(m_context, android.R.layout.select_dialog_item, android.R.id.text1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (v instanceof TextView) {
                    // Enable list item (directory) text wrapping
                    TextView tv = (TextView) v;
                    tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                    tv.setEllipsize(null);
                }
                return v;
            }
        };
    }

    //////////////////////////////////////////////////////
    // Callback interface for selected directory
    //////////////////////////////////////////////////////
    public interface SimpleFileDialogListener {
        public void onChosenDir(String chosenDir);
    }
} 
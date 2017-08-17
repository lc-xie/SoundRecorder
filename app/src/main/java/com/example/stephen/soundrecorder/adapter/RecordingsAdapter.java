package com.example.stephen.soundrecorder.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stephen.soundrecorder.CalculateDurationToTime;
import com.example.stephen.soundrecorder.R;
import com.example.stephen.soundrecorder.beans.RecordingFiles;
import com.example.stephen.soundrecorder.popup.PopupWindowDelete;
import com.example.stephen.soundrecorder.popup.PopupWindowRename;
import com.example.stephen.soundrecorder.fragment.SavedRecordingsFragment;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

/**
 * Created by stephen on 17-8-12.
 */

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {

    private List<RecordingFiles> recordingFileList;
    private SavedRecordingsFragment s;
    private boolean ifRenameClicked=false;
    private boolean ifDeleteClicked=false;
    PopupWindowRename popupWindowRename;
    PopupWindowDelete popupWindowDelete;
    public static final String PLAY_DIALOG="com.example.stephen.soundrecorder.action.PLAY_DIALOG";
    public static final String NOTIFY_RECYCLER_VIEW="com.example.stephen.soundrecorder.action.NOTIFY_RECYCLER_VIEW";
    final File fileDir=new File(Environment.getExternalStorageDirectory(),"/RecordFile");

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView fileName,fileLength,fileData;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            fileName=(TextView) itemView.findViewById(R.id.name_recording_file);
            fileLength=(TextView)itemView.findViewById(R.id.length_recording_file);
            fileData=(TextView)itemView.findViewById(R.id.data_recording_file);
        }
    }

    public RecordingsAdapter(List<RecordingFiles> list, SavedRecordingsFragment s) {
        this.recordingFileList =list;
        this.s=s;
    }

    /**
     * 按键响应
     * 点击item播放录音
     * 长按item滑出底部dialog，选择删除/重命名操作
     * 点击删除/重命名底部dialog消失，弹出删除/重命名popupWindow
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_recordings,parent,false);

        final ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                RecordingFiles recordingFiles = recordingFileList.get(position);
                Gson gson=new Gson();
                String recordJsonStr= gson.toJson(recordingFiles);
                Intent intent = new Intent(PLAY_DIALOG);
                intent.putExtra("record_json",recordJsonStr);
                v.getContext().sendBroadcast(intent);
            }
        });
        viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = viewHolder.getAdapterPosition();
                final RecordingFiles recordingFiles = recordingFileList.get(position);
                final Dialog bottomDialog=new Dialog(v.getContext(),R.style.BottomDialog);
                LinearLayout root=(LinearLayout)LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_delete_rename_files,null);
                //初始化底部dialog视图,设置按键响应
                //删除按键
                root.findViewById(R.id.delete_recording_file).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomDialog.dismiss();//隐藏底部dialog
                        popupWindowDelete=new PopupWindowDelete(v.getContext(),itemsOnClick,recordingFiles.getFileName());
                        popupWindowDelete.showAtLocation(v,Gravity.CENTER,0,0);
                        //设置背景模糊，需要使用getActivity().getWindow(),所以需要在adapter构造器中传入fragment实例
                        setWindowAlpha(0.6f);
                        //bottomPopupWindow消失后恢复fragment透明度
                        //并判断是否点击了确定按钮
                        //若点击了，就执行删除文件操作
                        popupWindowDelete.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                setWindowAlpha(1f);
                                if (ifDeleteClicked){
                                    File nowFile=new File(fileDir,recordingFiles.getFileName());
                                    if (nowFile.exists()){
                                        nowFile.delete();
                                        Toast.makeText(s.getContext(),nowFile.getName()+"文件已删除！",Toast.LENGTH_SHORT).show();
                                        //发送广播更新recycler view
                                        Intent intent=new Intent(NOTIFY_RECYCLER_VIEW);
                                        s.getContext().sendBroadcast(intent);
                                    }
                                    ifDeleteClicked=false;
                                }
                            }
                        });
                    }
                });
                root.findViewById(R.id.rename_recording_file).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomDialog.dismiss();
                        popupWindowRename =new PopupWindowRename(v.getContext(),itemsOnClick,recordingFiles.getFileName().substring(0,recordingFiles.getFileName().length()-4));
                        popupWindowRename.showAtLocation(v,Gravity.CENTER,0,0);
                        //设置背景模糊，需要使用getActivity().getWindow(),所以需要在adapter构造器中传入fragment实例
                        setWindowAlpha(0.6f);
                        //bottomPopupWindow消失后恢复fragment透明度
                        //并判断是否点击了确定按钮
                        //若点击了，就执行重命名操作
                        popupWindowRename.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                setWindowAlpha(1f);
                                if (ifRenameClicked){
                                    File nowFile=new File(fileDir,recordingFiles.getFileName());
                                    Log.d("RecorderAdapter",nowFile.getAbsolutePath());
                                    if (nowFile.exists()){
                                        Log.d("RecorderAdapter","file exist!!!");
                                        File renameFile=new File(fileDir, popupWindowRename.getEditText()+".mp3");
                                        nowFile.renameTo(renameFile);
                                        //发送广播更新recycler view
                                        Intent intent=new Intent(NOTIFY_RECYCLER_VIEW);
                                        s.getContext().sendBroadcast(intent);
                                    }
                                    ifRenameClicked=false;
                                }
                            }
                        });
                    }
                });
                bottomDialog.setContentView(root);
                Window dialogWindow = bottomDialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                lp.x = 0; // 新位置X坐标
                lp.y = 0; // 新位置Y坐标
                lp.width = (int) v.getContext().getResources().getDisplayMetrics().widthPixels; // 宽度
                root.measure(0, 0);
                lp.height = root.getMeasuredHeight();
                lp.alpha = 9f; // 透明度
                dialogWindow.setAttributes(lp);
                bottomDialog.show();
                return true;
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecordingFiles recordingFiles=recordingFileList.get(position);
        holder.fileData.setText(recordingFiles.getFileData());
        holder.fileName.setText(recordingFiles.getFileName());
        holder.fileLength.setText(CalculateDurationToTime.calTime(recordingFiles.getFileLength()));
    }

    @Override
    public int getItemCount() {
        return recordingFileList.size();
    }

    public View.OnClickListener itemsOnClick =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.popup_rename_ok:
                    ifRenameClicked=true;
                    popupWindowRename.dismiss();
                    break;
                case R.id.popup_rename_cancel:
                    ifRenameClicked=false;
                    popupWindowRename.dismiss();
                    break;
                case R.id.popup_delete_ok:
                    ifDeleteClicked=true;
                    popupWindowDelete.dismiss();
                    break;
                case R.id.popup_delete_cancel:
                    ifDeleteClicked=false;
                    popupWindowDelete.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    //设置fragment透明度
    public void setWindowAlpha(float f){
        WindowManager.LayoutParams lp=s.getActivity().getWindow().getAttributes();
        lp.alpha = f; //0.0-1.0
        s.getActivity().getWindow().setAttributes(lp);
    }
}

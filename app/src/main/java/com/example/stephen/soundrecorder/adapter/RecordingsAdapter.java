package com.example.stephen.soundrecorder.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stephen.soundrecorder.CalculateDurationToTime;
import com.example.stephen.soundrecorder.R;
import com.example.stephen.soundrecorder.beans.RecordingFiles;
import com.example.stephen.soundrecorder.popup.MultipleDeletePopup;
import com.example.stephen.soundrecorder.popup.PopupWindowDelete;
import com.example.stephen.soundrecorder.popup.PopupWindowRename;
import com.example.stephen.soundrecorder.fragment.SavedRecordingsFragment;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 17-8-12.
 * 文件列表recyclerView适配器
 */

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {

    private List<RecordingFiles> recordingFileList;
    private static List<RecordingFiles> multipleFileList=new ArrayList<>();//多选选择的文件集合
    private SavedRecordingsFragment s;
    private boolean ifRenameClicked=false;
    private boolean ifDeleteClicked=false;
    private boolean ifMultipleDeleteClicked=false;
    private PopupWindowRename popupWindowRename;//重命名popupWindow
    private PopupWindowDelete popupWindowDelete;//确认删除popupWindow
    private MultipleDeletePopup multipleBottomDialog;//多选删除popupWindow
    private static final String PLAY_DIALOG="com.example.stephen.soundrecorder.action.PLAY_DIALOG";
    private static final String NOTIFY_RECYCLER_VIEW="com.example.stephen.soundrecorder.action.NOTIFY_RECYCLER_VIEW";
    //录音文件存储父路径
    final private File fileDir=new File(Environment.getExternalStorageDirectory(),"/RecordFile");

    private Dialog bottomDialog;//底部选择dialog(delete,rename,share,multiple)
    private boolean ifMultiple=false;//是否多选

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView fileName,fileLength,fileData;
        CheckBox checkBox;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            fileName=(TextView) itemView.findViewById(R.id.name_recording_file);
            fileLength=(TextView)itemView.findViewById(R.id.length_recording_file);
            fileData=(TextView)itemView.findViewById(R.id.data_recording_file);
            checkBox=(CheckBox)itemView.findViewById(R.id.check_recording_file);
        }
    }

    public RecordingsAdapter(List<RecordingFiles> list, SavedRecordingsFragment s) {
        this.recordingFileList =list;
        this.s=s;
    }

    /**
     * 按键响应,点击item播放录音
     * 长按item滑出底部dialog，选择删除/重命名操作
     * @param parent
     * @param viewType
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_recordings,parent,false);

        final ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //若在多选删除情况下，点击item选择/取消checkbox，不能播放
                if (ifMultiple){
                    if (viewHolder.checkBox.isChecked()){
                        viewHolder.checkBox.setChecked(false);
                    }else viewHolder.checkBox.setChecked(true);
                    return;
                }
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
                if (!ifMultiple){
                    longClickListener(viewHolder);
                }
                return true;
            }
        });
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = viewHolder.getAdapterPosition();
                RecordingFiles selectedRecordingFiles = recordingFileList.get(selectedPosition);
                if (viewHolder.checkBox.isChecked()){
                    //若被选中
                    multipleFileList.add(selectedRecordingFiles);
                }else {
                    multipleFileList.remove(selectedRecordingFiles);
                }
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
        if (ifMultiple){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }else {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return recordingFileList.size();
    }

    private View.OnClickListener itemsOnClick =new View.OnClickListener() {
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
                case R.id.cancel_multiple_recording_file:
                    multipleBottomDialog.dismiss();
                    break;
                case R.id.delete_multiple_recording_file:
                    ifMultipleDeleteClicked=true;
                    multipleBottomDialog.dismiss();
                default:
                    break;
            }
        }
    };
    //长按item弹出底部popuWindow
    private void longClickListener(ViewHolder viewHolder){
            int position = viewHolder.getAdapterPosition();
            final RecordingFiles recordingFiles = recordingFileList.get(position);//选中的文件
            bottomDialog=new Dialog(s.getContext(),R.style.BottomDialog);
            LinearLayout root=(LinearLayout)LayoutInflater.from(s.getContext()).inflate(R.layout.bottom_dialog,null);
            //初始化底部dialog视图,设置按键响应
            //分享按键
            root.findViewById(R.id.multiple_recording_file).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    multipleDeleteFile();
                }
            });
            //分享按键
            root.findViewById(R.id.share_recording_file).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareFile(new File(fileDir,recordingFiles.getFileName()));
                }
            });
            //删除按键
            root.findViewById(R.id.delete_recording_file).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFile(new File(fileDir,recordingFiles.getFileName()));
                }
            });
            //重命名按键
            root.findViewById(R.id.rename_recording_file).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renameFile(new File(fileDir,recordingFiles.getFileName()));
                }
            });
            bottomDialog.setContentView(root);
            Window dialogWindow = bottomDialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = 0; // 新位置Y坐标
            lp.width = (int) s.getContext().getResources().getDisplayMetrics().widthPixels; // 宽度
            root.measure(0, 0);
            lp.height = root.getMeasuredHeight();
            lp.alpha = 9f; // 透明度
            dialogWindow.setAttributes(lp);
            bottomDialog.show();
        }
    //删除多选的文件
    public void multipleDeleteFile(){
        bottomDialog.dismiss();//隐藏底部dialog
        ifMultiple=true;//设置多选框可见
        //发送广播更新recycler view
        Intent intent=new Intent(NOTIFY_RECYCLER_VIEW);
        s.getContext().sendBroadcast(intent);
        //弹出底部删除/取消选择框
        multipleBottomDialog=new MultipleDeletePopup(s.getContext(),itemsOnClick);
        multipleBottomDialog.showAtLocation(s.getView(),Gravity.BOTTOM,0,0);
        multipleBottomDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (ifMultipleDeleteClicked){
                    for (RecordingFiles recordings:multipleFileList){
                        File tempFile= new File(fileDir,recordings.getFileName());
                        if (tempFile.exists())tempFile.delete();
                    }
                    ifMultipleDeleteClicked=false;
                }
                //清空multipleFileList
                multipleFileList.clear();
                ifMultiple=false;
                //发送广播更新recycler view
                Intent intent=new Intent(NOTIFY_RECYCLER_VIEW);
                s.getContext().sendBroadcast(intent);
            }
        });
    }
    //分享文件
    private void shareFile(File file){
        bottomDialog.dismiss();//隐藏底部dialog
        setWindowAlpha(0.6f);//设置背景模糊，需要使用getActivity().getWindow(),所以需要在adapter构造器中传入fragment实例
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("audio/mp4");
        s.getContext().startActivity(Intent.createChooser(shareIntent,"发送至"));
        setWindowAlpha(1f);
    }
    //删除文件
    private void deleteFile(final File file){
        bottomDialog.dismiss();//隐藏底部dialog
        popupWindowDelete=new PopupWindowDelete(s.getContext(),itemsOnClick,file.getName());
        popupWindowDelete.showAtLocation(s.getView(),Gravity.CENTER,0,0);
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
                    if (file.exists()){
                        file.delete();
                        Toast.makeText(s.getContext(),file.getName()+"文件已删除！",Toast.LENGTH_SHORT).show();
                        //发送广播更新recycler view
                        Intent intent=new Intent(NOTIFY_RECYCLER_VIEW);
                        s.getContext().sendBroadcast(intent);
                    }
                    ifDeleteClicked=false;
                }
            }
        });
    }
    //重命名文件
    private void renameFile(final File file){
        bottomDialog.dismiss();
        popupWindowRename =new PopupWindowRename(s.getContext(),itemsOnClick,file.getName().substring(0,file.getName().length()-4));
        popupWindowRename.showAtLocation(s.getView(),Gravity.CENTER,0,0);
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
                    Log.d("RecorderAdapter",file.getAbsolutePath());
                    if (file.exists()){
                        Log.d("RecorderAdapter","file exist!!!");
                        File renameFile=new File(fileDir, popupWindowRename.getEditText()+".mp3");
                        file.renameTo(renameFile);
                        //发送广播更新recycler view
                        Intent intent=new Intent(NOTIFY_RECYCLER_VIEW);
                        s.getContext().sendBroadcast(intent);
                    }
                    ifRenameClicked=false;
                }
            }
        });
    }

    //设置fragment透明度
    private void setWindowAlpha(float f){
        WindowManager.LayoutParams lp=s.getActivity().getWindow().getAttributes();
        lp.alpha = f; //0.0-1.0
        s.getActivity().getWindow().setAttributes(lp);
    }
}

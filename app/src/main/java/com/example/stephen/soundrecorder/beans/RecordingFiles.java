package com.example.stephen.soundrecorder.beans;

/**
 * Created by stephen on 17-8-12.
 */

public class RecordingFiles {
    private String fileName;
    private int fileLength;
    private String fileData;

    public RecordingFiles(String name,int len,String data) {
        this.fileData=data;
        this.fileLength=len;
        this.fileName=name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileLength() {
        return fileLength;
    }

    public void setFileLength(int fileLength) {
        this.fileLength = fileLength;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }
}

package com.leone.boot.common.design.structure.bridge;

public class DiskDriver implements Driver {
 
    @Override
    public void connect() {
        System.out.println("connect db2 done!");
    }
}
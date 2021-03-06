package com.hjq.permissions.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void requestPermission(View view) {
        XXPermissions.with(this)
                .permission(Permission.Group.STORAGE)
                .request(new OnPermission() {

                    @Override
                    public void hasPermission(List<String> granted) {
                        Toast.makeText(MainActivity.this, "获取SD卡读取写入权限成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean permanent) {
                        if(permanent) {
                            Toast.makeText(MainActivity.this, "被永久拒绝授权，请手动授予权限", Toast.LENGTH_SHORT).show();
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            PermissionUtils.gotoPermissionSettings(MainActivity.this);
                        }else {
                            Toast.makeText(MainActivity.this, "获取SD卡读取写入权限失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void isHasPermission(View view) {
        if (PermissionUtils.isHasPermission(MainActivity.this, Permission.Group.STORAGE)) {
            Toast.makeText(MainActivity.this, "已经获取到SD卡读写权限，不需要再次申请了", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(MainActivity.this, "还没有获取到SD卡读写权限", Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoPermissionSettings(View view) {
        PermissionUtils.gotoPermissionSettings(MainActivity.this);
    }
}

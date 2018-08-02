package com.mapuni.gdydcaiji.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
<<<<<<< HEAD
import android.content.pm.PackageManager;
=======
>>>>>>> 746d730cb42a41f26875c11a1735a2e36e6a7075
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
<<<<<<< HEAD
import android.widget.TextView;
=======
>>>>>>> 746d730cb42a41f26875c11a1735a2e36e6a7075
import android.widget.Toast;

import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.LoginBean;
import com.mapuni.gdydcaiji.net.RetrofitFactory;
import com.mapuni.gdydcaiji.net.RetrofitService;
import com.mapuni.gdydcaiji.utils.LogUtils;
<<<<<<< HEAD
import com.mapuni.gdydcaiji.utils.PermissionUtils;
=======
>>>>>>> 746d730cb42a41f26875c11a1735a2e36e6a7075
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;

import java.sql.RowId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.iv_username)
    ImageView ivUsername;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.iv_password)
    ImageView ivPassword;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.cb_remember)
    CheckBox cbRemember;
<<<<<<< HEAD
    @BindView(R.id.version_name)
    TextView versionText;
=======
>>>>>>> 746d730cb42a41f26875c11a1735a2e36e6a7075
    private String spUsername;
    private String spPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
<<<<<<< HEAD
        PermissionUtils.requestAllPermission(this);
=======
>>>>>>> 746d730cb42a41f26875c11a1735a2e36e6a7075
        // 用户名&密码回显
        boolean isRemember = SPUtils.getInstance().getBoolean("isRemember", false);
        cbRemember.setChecked(isRemember);
        spUsername = SPUtils.getInstance().getString("username", "");
        spPassword = SPUtils.getInstance().getString("password", "");
        if (isRemember) {
            //LogUtils.d(password);
            etUsername.setText(spUsername);
            etPassword.setText(spPassword);

        }
<<<<<<< HEAD
        try {
            versionText.setText(getPackageManager().getPackageInfo(getPackageName(),0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
=======
>>>>>>> 746d730cb42a41f26875c11a1735a2e36e6a7075

    }


    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        submit();
    }


    /**
     *
     */
    private void submit() {
        // validate
        String username = etUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 登录
        login(username, password);

    }

    /**
     * 联网登录
     *
     * @param username
     * @param password
     */
    private void login(final String username, final String password) {
        if (username.equals(spUsername) && password.equals(spPassword)) {
            String roleid = SPUtils.getInstance().getString("roleid");
            if ("2".equals(roleid) || "6".equals(roleid) || "8".equals(roleid)) {
                ToastUtils.showShort("登录成功");
                // 保存信息
                boolean isChecked = cbRemember.isChecked();
                if (isChecked) {
                    // 记住密码
                    SPUtils.getInstance().put("isRemember", isChecked);
                } else {
                    // 不记住密码
                    SPUtils.getInstance().put("isRemember", isChecked);
                }

                startActivity(new Intent(LoginActivity.this, CollectionActivity.class));
                finish();
                return;
            }

        }
        final Call<LoginBean> call = RetrofitFactory.create(RetrofitService.class)
                .login(username, password);
        // Dialog
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("正在登录...");
        // 点击对话框以外的地方无法取消
        pd.setCanceledOnTouchOutside(false);
        // 点击返回按钮无法取消
        pd.setCancelable(false);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                call.cancel();
            }
        });
        pd.show();
        call.enqueue(new Callback<LoginBean>() {

            @Override
            public void onResponse(@NonNull Call<LoginBean> call, @NonNull Response<LoginBean> response) {
//                LogUtils.d("onResponse" + response.body());
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                LoginBean loginBean = response.body();
//                assert loginBean != null;
                if (loginBean != null) {
                    if ("登录成功".equals(loginBean.getMsg())) {
                        if (!"2".equals(loginBean.getUser().getRoleid()) && !"6".equals(loginBean.getUser().getRoleid()) && !"8".equals(loginBean.getUser().getRoleid())) {
                            ToastUtils.showShort("仅限外业或采集人员登录");
                            return;
                        }
                        ToastUtils.showShort("登录成功");
                        // 保存信息
                        boolean isChecked = cbRemember.isChecked();
                        if (isChecked) {
                            // 记住密码
                            SPUtils.getInstance().put("isRemember", isChecked);
                        } else {
                            // 不记住密码
                            SPUtils.getInstance().put("isRemember", isChecked);
//                            SPUtils.getInstance().put("username", "");
//                            SPUtils.getInstance().put("password", "");
                        }
                        SPUtils.getInstance().put("username", username);
                        SPUtils.getInstance().put("password", password);
                        SPUtils.getInstance().put("roleid", loginBean.getUser().getRoleid());
                        SPUtils.getInstance().put("userId", loginBean.getUser().getId());
//                        if("2".equals(loginBean.getUser().getRoleid())){
//                            //质检
//                            startActivity(new Intent(LoginActivity.this, QCListActivity.class));
//                        }else if("6".equals(loginBean.getUser().getRoleid())){
                        //外业
                        startActivity(new Intent(LoginActivity.this, CollectionActivity.class));
//                        }
                        finish();
                    } else {
                        ToastUtils.showShort(loginBean.getMsg());
                    }
                } else {
                    ToastUtils.showShort("服务器忙，请稍后重试");
                }
            }

            @Override
            public void onFailure(Call<LoginBean> call, Throwable t) {
                t.printStackTrace();
                if (!call.isCanceled()) {
                    // 非点击取消
                    LogUtils.d(t.getMessage());
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    ToastUtils.showShort("登录失败");
//                    BillInfo.deleteAuthorizeBill(LoginActivity.this);
                } else {
                    ToastUtils.showShort("取消登录");
                }

            }
        });

    }

}

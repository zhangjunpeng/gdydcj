package com.mapuni.gdydcaiji.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.LoginBean;
import com.mapuni.gdydcaiji.net.RetrofitFactory;
import com.mapuni.gdydcaiji.net.RetrofitService;
import com.mapuni.gdydcaiji.utils.LogUtils;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;
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
        if(username.equals(spUsername) && password.equals(spPassword)){
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
                        startActivity(new Intent(LoginActivity.this, CollectionActivity.class));
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

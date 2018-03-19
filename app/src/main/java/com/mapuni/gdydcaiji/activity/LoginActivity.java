package com.mapuni.gdydcaiji.activity;

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
import com.mapuni.gdydcaiji.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.btn_login)
    Button btnLogin;

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
        if (isRemember) {
            String username = SPUtils.getInstance().getString("username", "");
            String password = SPUtils.getInstance().getString("password", "");
            //LogUtils.d(password);
            etUsername.setText(username);
            etPassword.setText(password);

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
    private void login(String username, String password) {
//        final Call<LoginBean> call = RetrofitFactory.create(RetrofitService.class)
//                .login(username, password, bill);
//        // Dialog
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("正在登录...");
//        // 点击对话框以外的地方无法取消
//        pd.setCanceledOnTouchOutside(false);
//        // 点击返回按钮无法取消
//        pd.setCancelable(false);
//        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                call.cancel();
//            }
//        });
//        pd.show();
//        call.enqueue(new Callback<LoginBean>() {
//            @Override
//            public void onResponse(@NonNull Call<LoginBean> call, @NonNull Response<LoginBean> response) {
////                LogUtils.d("onResponse" + response.body());
//                if (pd.isShowing()) {
//                    pd.dismiss();
//                }
//                LoginBean loginBean = response.body();
////                assert loginBean != null;
//                if (loginBean != null) {
//                    if (loginBean.isFlag()) {
//                        ToastUtils.showShort("登录成功");
//                        // 保存信息
//                        SPUtils.getInstance().put("policeId", loginBean.getUser().getPoliceId());
//                        SPUtils.getInstance().put("roleType", loginBean.getUser().getRoleType());
//                        SPUtils.getInstance().put("userId", loginBean.getUser().getUserId() + "");
//                        boolean isChecked = cbRemember.isChecked();
//                        if (isChecked) {
//                            // 记住密码
//                            SPUtils.getInstance().put("isRemember", isChecked);
//                            SPUtils.getInstance().put("username", loginBean.getUser().getUserName());
//                            SPUtils.getInstance().put("password", loginBean.getUser().getPassword());
//                        } else {
//                            // 不记住密码
//                            SPUtils.getInstance().put("isRemember", isChecked);
//                            SPUtils.getInstance().put("username", "");
//                            SPUtils.getInstance().put("password", "");
//                        }
//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                        finish();
//                    } else {
//                        ToastUtils.showShort(loginBean.getMsg());
//                    }
//                } else {
//                    ToastUtils.showShort("服务器忙，请稍后重试");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginBean> call, Throwable t) {
//                t.printStackTrace();
//                if (!call.isCanceled()) {
//                    // 非点击取消
//                    LogUtils.d(t.getMessage());
//                    if (pd.isShowing()) {
//                        pd.dismiss();
//                    }
//                    ToastUtils.showShort("登录失败");
////                    BillInfo.deleteAuthorizeBill(LoginActivity.this);
//                } else {
//                    ToastUtils.showShort("取消登录");
//                }
//
//            }
//        });


    }

}

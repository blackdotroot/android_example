package app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.enjoyhelp.R;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class SignupActivity extends Activity implements OnWheelChangedListener  
{  
    /** 
     * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null 
     */  
    private JSONObject mJsonObj;  
    /** 
     * 省的WheelView控件 
     */  
    private WheelView mProvince;  
    /** 
     * 市的WheelView控件 
     */  
    private WheelView mCity;  
    /** 
     * 区的WheelView控件 
     */  
    private WheelView mArea;  
  
    /** 
     * 所有省 
     */  
    private String[] mProvinceDatas;  
    /** 
     * key - 省 value - 市s 
     */  
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();  
    /** 
     * key - 市 values - 区s 
     */  
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();  
    /** 
     * 当前省的名称 
     */  
    private String mCurrentProviceName;  
    /** 
     * 当前市的名称 
     */  
    private String mCurrentCityName;  
    /** 
     * 当前区的名称 
     */  
    private String mCurrentAreaName ="";  
    private ImageView user_pic;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private ImageView back;
    @Override  
    protected void onCreate(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.wheel_local);  
        //Toast.makeText(this, "test into singnup", 1).show();
        initJsonData();  
        mProvince = (WheelView) findViewById(R.id.id_province);  
        mCity = (WheelView) findViewById(R.id.id_city);  
        mArea = (WheelView) findViewById(R.id.id_area);  
        user_pic = (ImageView) findViewById(R.id.user_pic);
        user_pic.setOnClickListener(user_pic_BtnListener);
        back=(ImageView)findViewById(R.id.singup_back); 
        back.setOnClickListener(back_BtnListener);
        initDatas();  
        mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));  
        // 添加change事件  
        mProvince.addChangingListener(this);  
        // 添加change事件  
        mCity.addChangingListener(this);  
        // 添加change事件  
        mArea.addChangingListener(this);  
        mProvince.setVisibleItems(5);  
        mCity.setVisibleItems(5);  
        mArea.setVisibleItems(5);  
        updateCities();  
        updateAreas();  
    }  
    private View.OnClickListener back_BtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
    };
    
    private View.OnClickListener user_pic_BtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showChoosePicDialog();
		}
    };
    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case CHOOSE_PICTURE: // 选择本地照片
                    Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    openAlbumIntent.setType("image/*");
                    startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                    break;
                case TAKE_PICTURE: // 拍照
                    Intent openCameraIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    		tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                    // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                    startActivityForResult(openCameraIntent, TAKE_PICTURE);
                    break;
                }
            }
        });
        builder.create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
            case TAKE_PICTURE:
                startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                break;
            case CHOOSE_PICTURE:
                startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                break;
            case CROP_SMALL_PICTURE:
                if (data != null) {
                    setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                }
                break;
            }
        }
    }
    /**
     * 裁剪图片方法实现
     * 
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     * 
     * @param
     * 
     * @param picdata
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = Utils.toRoundBitmap(photo, tempUri); // 这个时候的图片已经被处理成圆形的了
            user_pic.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了

        String imagePath = Utils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("imagePath", imagePath+"");
        if(imagePath != null){
            // 拿着imagePath上传了
            // ...
        }
    }
    
    /** 
     * 根据当前的市，更新区WheelView的信息 
     */  
    private void updateAreas()  
    {  
        int pCurrent = mCity.getCurrentItem();  
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];  
        String[] areas = mAreaDatasMap.get(mCurrentCityName);  
        if (areas == null)  
        {  
            areas = new String[] { "" };  
        }  
        mArea.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));  
        mArea.setCurrentItem(0);  
    }  
  
    /** 
     * 根据当前的省，更新市WheelView的信息 
     */  
    private void updateCities()  
    {  
        int pCurrent = mProvince.getCurrentItem();  
        mCurrentProviceName = mProvinceDatas[pCurrent];  
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);  
        if (cities == null)  
        {  
            cities = new String[] { "" };  
        }  
        mCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));  
        mCity.setCurrentItem(0);  
        updateAreas();  
    }  
  
    /** 
     * 解析整个Json对象，完成后释放Json对象的内存 
     */  
    private void initDatas()  
    {  
        try  
        {  
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");  
            mProvinceDatas = new String[jsonArray.length()];  
            for (int i = 0; i < jsonArray.length(); i++)  
            {  
                JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象  
                String province = jsonP.getString("name");// 省名字  
                mProvinceDatas[i] = province;  
                JSONArray jsonCs = null;  
                try  
                {  
                    /** 
                     * Throws JSONException if the mapping doesn't exist or is 
                     * not a JSONArray. 
                     */  
                    jsonCs = jsonP.getJSONArray("city");  
                } catch (Exception e1)  
                {  
                    continue;  
                }  
                String[] mCitiesDatas = new String[jsonCs.length()];  
                for (int j = 0; j < jsonCs.length(); j++)  
                {  
                    JSONObject jsonCity = jsonCs.getJSONObject(j);  
                    String city = jsonCity.getString("name");// 市名字  
                    mCitiesDatas[j] = city;  
                    JSONArray jsonAreas = null;  
                    try  
                    {  
                        /** 
                         * Throws JSONException if the mapping doesn't exist or 
                         * is not a JSONArray. 
                         */  
                        jsonAreas = (JSONArray)jsonCity.getJSONArray("area");  
                    } catch (Exception e)  
                    {  
                        continue;  
                    }  
                    
                    String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区  
                    for (int k = 0; k < jsonAreas.length(); k++)  
                    {  
                        //String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称  
                    	//String area = jsonAreas.getJSONObject(k).toString();
                    	String area=jsonAreas.getString(k);
                        mAreasDatas[k] = area;  
                    }  
                    mAreaDatasMap.put(city, mAreasDatas);  
                }  
                mCitisDatasMap.put(province, mCitiesDatas);  
            }  
  
        } catch (JSONException e)  
        {  
            e.printStackTrace();  
        }  
        mJsonObj = null;  
    }  
  
    /** 
     * 从assert文件夹中读取省市区的json文件，然后转化为json对象 
     */  
    private void initJsonData()  
    {  
        try  
        {  
            StringBuffer sb = new StringBuffer();  
            InputStream is = getAssets().open("city.json");  
            int len = -1;  
            byte[] buf = new byte[2048];  
            while ((len = is.read(buf)) != -1)  
            {  
                sb.append(new String(buf, 0, len, "gbk"));  
            }  
            is.close();  
            mJsonObj = new JSONObject(sb.toString());  
        } catch (IOException e)  
        {  
            e.printStackTrace();  
        } catch (JSONException e)  
        {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * change事件的处理 
     */  
    @Override  
    public void onChanged(WheelView wheel, int oldValue, int newValue)  
    {  
        if (wheel == mProvince)  
        {  
            updateCities();  
        } else if (wheel == mCity)  
        {  
            updateAreas();  
        } else if (wheel == mArea)  
        {  
            mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[newValue];  
        }  
    }  
  
    public void showChoose(View view)  
    {  
        Toast.makeText(this, mCurrentProviceName + mCurrentCityName + mCurrentAreaName, 1).show();  
    }

}  

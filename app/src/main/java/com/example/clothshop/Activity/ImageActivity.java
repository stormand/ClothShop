package com.example.clothshop.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Random;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String[] mImageArray;
    private int enterPosition;
    private static FloatingActionButton fab;

    private ViewGroup mPointGroup;
    private ImageView[] imageViews;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Bundle bundle= getIntent().getBundleExtra("showImage");
        mImageArray=bundle.getStringArray("imageArray");
        enterPosition=bundle.getInt("position");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(enterPosition);
        mViewPager.setOnPageChangeListener(new GuidePageChangeListener());
        //小白点
        mPointGroup= (ViewGroup) findViewById(R.id.detail_point_view_Group);
        initPointer();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                int currentItem=mViewPager.getCurrentItem();
                String url=mImageArray[currentItem];
                StringBuilder sb=new StringBuilder();
                if (url!=null && url.length()>2) {
                    sb.append(Model.IMAGE_SAVE_PATH)
                            .append(url.substring(2));
                    String imageurl = sb.toString();
                    final PhotoView photoView = new PhotoView(ImageActivity.this);
                    Picasso.with(ImageActivity.this).load(imageurl).into(photoView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
                            if (bmap == null) {
                                Snackbar.make(view, "empty image", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                                return;
                            }
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                //寻找储存路径
                                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                                File file = new File(sdCardPath + "/ClothShop/ImageDownload");
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                //日期时间随机数
                                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                String date = sDateFormat.format(new java.util.Date());
                                Random random=new Random();
                                File imageFile = new File(file.getAbsolutePath(), date + " " + random.nextInt(100) + ".jpg");
                                FileOutputStream outStream = null;
                                try {
                                    outStream = new FileOutputStream(imageFile);
                                    bmap.compress(Bitmap.CompressFormat.PNG, 90, outStream);
                                    outStream.flush();
                                    outStream.close();
                                    Snackbar.make(view, "dowmload success", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                } catch (Exception e) {
                                    Log.e("img_download","e");
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError() {

                        }

                    });
                }

            }
        });
    }

    public static void showFab(boolean show){
        if (show){
            fab.show();
        }else{
            fab.hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(getIntent().getBundleExtra("showImage").getInt("position"));
    }

    private void initPointer() {
        //有多少个界面就new多长的数组
        mPointGroup.removeAllViews();
        imageViews = new ImageView[mImageArray.length];
        for (int i = 0; i < imageViews.length; i++) {
            ImageView imageView = new ImageView(this);
            //设置控件的宽高
            //imageView.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
            //设置控件的padding属性
            LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(20, 20);
            lp.leftMargin=10;
            lp.rightMargin=10;
            imageView.setLayoutParams(lp);
            imageViews[i] = imageView;
            //初始化第一个page页面的图片的原点为选中状态
            if (i == 0) {
                //表示当前图片
                imageViews[i].setBackgroundResource(R.drawable.point_focused);
                /**
                 * 在java代码中动态生成ImageView的时候
                 * 要设置其BackgroundResource属性才有效
                 * 设置ImageResource属性无效
                 */
            } else {
                imageViews[i].setBackgroundResource(R.drawable.point_not_focused);
            }
            mPointGroup.addView(imageViews[i]);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String IMAGE_URL = "image_url";
        private boolean showFab=false;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String imageUrl) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(IMAGE_URL, imageUrl);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_image, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            final PhotoView photoView= (PhotoView) rootView.findViewById(R.id.photo_view);
            StringBuilder sb=new StringBuilder();
            String url=getArguments().getString(IMAGE_URL);
            if (url!=null && url.length()>2){
                sb.append(Model.IMAGE_SAVE_PATH)
                        .append(url.substring(2));
                String imageurl = sb.toString();
                Picasso.with(getActivity()).load(imageurl).placeholder(R.drawable.empty_image).error(R.drawable.error_image).into(photoView);
            }else {
                Picasso.with(getActivity()).load(R.drawable.empty_image).into(photoView);
            }
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    ImageActivity.showFab(showFab);
                    showFab=!showFab;
                }
            });
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(mImageArray[position]);
        }

        @Override
        public int getCount() {
            return mImageArray.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    public class GuidePageChangeListener implements ViewPager.OnPageChangeListener {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        //页面滑动完成后执行
        @Override
        public void onPageSelected(int position) {
            //判断当前是在那个page，就把对应下标的ImageView原点设置为选中状态的图片
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[position].setBackgroundResource(R.drawable.point_focused);
                if (position != i) {
                    imageViews[i].setBackgroundResource(R.drawable.point_not_focused);
                }
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}

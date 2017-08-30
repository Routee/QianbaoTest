package exocr.bankcard;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

/* CardScanner.java
 * See the file "LICENSE.md" for the full license governing this code.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Encapsulates the core image scanning.
 * 
 * As of 7/20/12, the flow should be:
 * 
 * 1. CardIOActivity sets up the CardScanner, Preview and Overlay. 
 * 2. As each frame is received & processed by the scanner, the scanner notifies the activity of any relevant changes. (e.g. edges
 *    detected, scan complete etc.) 
 * 3. CardIOActivity passes on the information to the preview and overlay, which can then update themselves as needed. 
 * 4. Once a result is reported, CardIOActivty closes the scanner and launches the next activity.
 * 
 * HOWEVER, at the moment, the CardScanner is directly communicating with the Preview.
 * 
 */
class CardScanner implements Camera.PreviewCallback, Camera.AutoFocusCallback, SurfaceHolder.Callback {
    private static final String TAG = CardScanner.class.getSimpleName();

//    private static final float MIN_FOCUS_SCORE = 3.5f;
    private static final float MIN_FOCUS_SCORE = 5.0f;
    private static final long AUTOFOCUS_TIMEOUT 				= 1000;
    private static final long MINIMUM_TIME_BETWEEN_DETECTIONS 	= 3000;
    private static final int CAMERA_CONNECT_TIMEOUT 			= 5000;
    private static final int CAMERA_CONNECT_RETRY_INTERVAL 		= 50;
    
    static final int ORIENTATION_PORTRAIT = 1;
    static final int ORIENTATION_PORTRAIT_UPSIDE_DOWN = 2;
    static final int ORIENTATION_LANDSCAPE_RIGHT = 3;
    static final int ORIENTATION_LANDSCAPE_LEFT = 4;
    static final int ORIENTATION_OFFSET = 100;
    ////////////////////////////////////////////////////////////////////////
    private Bitmap cardBitmap;
    // member data
    protected WeakReference<CardRecoActivity> mScanActivityRef;

    private long mAutoFocusStartedAt = 0;
    private long mAutoFocusCompletedAt = 0;

    private Camera mCamera = null;
    private byte[] mPreviewBuffer;

    // accessed by test harness subclass.
    protected boolean useCamera = true;
    private boolean isSurfaceValid = false;
    private boolean isFirstFrame = true;

    private Point screenResolution;
    private Point cameraResolution;
    private int previewFormat;
    private int previewFps;
    // read by CardIOActivity to set up Preveiw
    public int mPreviewWidth;
    public int mPreviewHeight;
    private final Context context;
    
    private int mLastDegrees;
    private int mFrameOrientation = ORIENTATION_PORTRAIT;
    private int previousOrientation;
    private boolean LOW_RESOLUTION = false;
    
    private final String lastBankName = null;
    
    List<Size> mSupportedPreviewSizes;
   
    //recognition valuables
    private EXBankCardInfo mCardInfo = null;
	public byte []bResultBuf;
	public int    nResultLen;
	public static final int mMaxStreamBuf = 1024;
	
	//////////////////////////////////////////////
	private boolean flagFocused;
//	public boolean bCapture;
    
	private Handler handler = new Handler();
    private ExecutorService executorService = null;
	
    void createExecutor() {
    	int recognizerNumber = Math.min(3, Runtime.getRuntime().availableProcessors());
		executorService = Executors.newFixedThreadPool(recognizerNumber);
    }
    /** Construction */
    CardScanner(CardRecoActivity scanActivity) {
    	
    	Log.i("DEBUG_TIME", "CardScanner_CardScanner="+System.currentTimeMillis());
    	
        Intent scanIntent = scanActivity.getIntent();
        mScanActivityRef = new WeakReference<CardRecoActivity>(scanActivity);
        cardBitmap = null;
        context = scanActivity.getApplicationContext();
        //screenResolution = new Point(0, 0);
        
        mCardInfo = new EXBankCardInfo(); 
		bResultBuf = new byte[mMaxStreamBuf];
		nResultLen = 0;
		flagFocused = false;
		
		createExecutor();
		bHasResult = false;
		
		EXBankCardReco.nativeCheckSignature(context);
    }
    
    CardScanner(CardRecoActivity scanActivity, int currentFrameOrientation) {
        Intent scanIntent = scanActivity.getIntent();
        mScanActivityRef = new WeakReference<CardRecoActivity>(scanActivity);
        mFrameOrientation = currentFrameOrientation;
        cardBitmap = null;
        context = scanActivity.getApplicationContext();
        //screenResolution = new Point(0, 0);
        
        mCardInfo = new EXBankCardInfo(); 
		bResultBuf = new byte[mMaxStreamBuf];
		nResultLen = 0;
		flagFocused = false;
		
		createExecutor();
		bHasResult = false;
		
		EXBankCardReco.nativeCheckSignature(context);
    }
    
    /** Reads, one time, values from the camera that are needed by the app. */
    void initFromCameraParameters(Camera camera) {
      Camera.Parameters parameters = camera.getParameters();
      previewFormat = parameters.getPreviewFormat();
      
	  WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
      Display display = manager.getDefaultDisplay();
      
      //screenResolution = new Point(800, 480);
      //cameraResolution = new Point(800, 480);
      //return;
      //display.getRealSize(screenResolution);
      screenResolution = getRealScreenSize();
      Log.d(TAG, "Screen resolution: " + screenResolution);
      
      //hold previewSizes
      mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
      if (screenResolution.y > screenResolution.x) {
    	  cameraResolution = getOptimalPreviewSize(mSupportedPreviewSizes, screenResolution.y, screenResolution.x);
      } else {		//适配低版本系统
    	  cameraResolution = getOptimalPreviewSize(mSupportedPreviewSizes, screenResolution.x, screenResolution.y);
      }
//      cameraResolution = new Point(1280, 720);
    }
    //get real screen size
	private Point getRealScreenSize() {
		int heightPixels, widthPixels;
		Point screenResolution = null;
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = manager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		// since SDK_INT = 1;
		heightPixels = metrics.heightPixels;
		widthPixels  = metrics.widthPixels;
		// includes window decorations (statusbar bar/navigation bar)
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
			try {
				heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
				widthPixels  = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
			} catch (Exception ignored) {
			}
		// includes window decorations (statusbar bar/navigation bar)
		else if (Build.VERSION.SDK_INT >= 17)
			try {
				android.graphics.Point realSize = new android.graphics.Point();
				Display.class.getMethod("getRealSize", android.graphics.Point.class).invoke(d, realSize);
				heightPixels = realSize.y;
				widthPixels = realSize.x;
			} catch (Exception ignored) {
				
			}
		//Log.e("realHightPixels-heightPixels", heightPixels + "width");
		screenResolution = new Point(widthPixels, heightPixels); 
		return screenResolution;
	}
	
    /** Get optimal preview size*/
    private Point getOptimalPreviewSize(List<Size> sizes, int w, int h) {    	
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
        Point cameraResolution = null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        
        cameraResolution = new Point(optimalSize.width, optimalSize.height);  
        //cameraResolution = new Point(640, 480);  
        return cameraResolution;
    }
    
    /**
     * Sets the camera up to take preview images which are used for both preview and decoding.
     * We detect the preview format here so that buildLuminanceSource() can build an appropriate
     * LuminanceSource subclass. In the future we may want to force YUV420SP as it's the smallest,
     * and the planar Y can be used for barcode scanning without a copy in some cases.
     */
    void setDesiredCameraParameters(Camera camera) {
      Camera.Parameters parameters = camera.getParameters();
      Log.d(TAG, "Setting preview size: " + cameraResolution);
      parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);     
      camera.setParameters(parameters);
    }

    /**
     * Connect or reconnect to camera. If fails, sleeps and tries again. Returns <code>true</code> if successful,
     * <code>false</code> if maxTimeout passes.
     */
    private Camera connectToCamera(int checkInterval, int maxTimeout) {
        long start = System.currentTimeMillis();
        if (useCamera) {
            do {
                try { // Camera.open() will open the back-facing camera. Front cameras are not attempted.
                    return Camera.open();
                } catch (RuntimeException e) {
                    try {
                        Log.w(TAG, "Wasn't able to connect to camera service. Waiting and trying again...");
                        Thread.sleep(checkInterval);
                    } catch (InterruptedException e1) {
                        Log.e(TAG, "Interrupted while waiting for camera", e1);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Unexpected exception. Please report it to support@card.io", e);
                    maxTimeout = 0;
                }
            } while (System.currentTimeMillis() - start < maxTimeout);
        }
        Log.w(TAG, "camera connect timeout");
        return null;
    }

    /** prepare Scanner  */
    void prepareScanner() {
        Log.v(TAG, "prepareScanner()");
        mAutoFocusStartedAt = 0;
        mAutoFocusCompletedAt = 0;
        Log.i("DEBUG_TIME", "CardScanner_prepareScanner1="+System.currentTimeMillis());
        //open camera
        if (useCamera && mCamera == null) {
            mCamera = connectToCamera(CAMERA_CONNECT_RETRY_INTERVAL, CAMERA_CONNECT_TIMEOUT);
            if (mCamera == null) {
                Log.e(TAG, "prepare scanner couldn't connect to camera!");
                return;
            } else {
                Log.v(TAG, "camera is connected");
            }
            
            mCamera.setDisplayOrientation(90);
            
            Log.i("DEBUG_TIME", "CardScanner_prepareScanner2="+System.currentTimeMillis());
            //init camera parameters
            initFromCameraParameters(mCamera);
            mPreviewWidth = cameraResolution.x;
            mPreviewHeight= cameraResolution.y;
            if (mPreviewHeight < 720) {
            	LOW_RESOLUTION = true;
            }
            setDesiredCameraParameters(mCamera);
            
            Log.i("DEBUG_TIME", "CardScanner_prepareScanner3="+System.currentTimeMillis());
        } else if (!useCamera) {
            Log.w(TAG, "useCamera is false!");
        } else if (mCamera != null) {
            Log.v(TAG, "we already have a camera instance: " + mCamera);
        }
    }
    
    
    /** */
    @SuppressWarnings("deprecation")
    boolean resumeScanning(SurfaceHolder holder) {
    	Log.i("DEBUG_TIME", "CardScanner_resumeScanning1="+System.currentTimeMillis());
        Log.v(TAG, "resumeScanning(" + holder + ")");
        if (mCamera == null) {
            Log.v(TAG, "preparing the scanner...");
            prepareScanner();
            Log.v(TAG, "preparations complete");
        }
        if (useCamera && mCamera == null) {
            // prepare failed!
            Log.i(TAG, "null camera. failure");
            return false;
        }
        
        flagFocused = false;
        assert holder != null;
        //set 
        //setDesiredCameraParameters(mCamera);
        Log.i("DEBUG_TIME", "CardScanner_resumeScanning2="+System.currentTimeMillis());

        if (executorService == null) {
        	createExecutor();
        }
        bHasResult = false;
        
        if (useCamera && mPreviewBuffer == null) {
            int previewFormat = ImageFormat.NV21; // the default.
            Camera.Parameters parameters = mCamera.getParameters();
            previewFormat = parameters.getPreviewFormat();
            int bytesPerPixel = ImageFormat.getBitsPerPixel(previewFormat) / 8; 
            int bufferSize = mPreviewWidth * mPreviewHeight * bytesPerPixel * 3;
            mPreviewBuffer = new byte[bufferSize];
            mCamera.addCallbackBuffer(mPreviewBuffer);
        }

        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (useCamera){
        	mCamera.setPreviewCallbackWithBuffer(this); 
        }        
        if (isSurfaceValid) {
        	makePreviewGo(holder);
        }

        Log.i("DEBUG_TIME", "CardScanner_resumeScanning3="+System.currentTimeMillis());
        // Turn flash off
        //setFlashOn(false);
        Log.i("DEBUG_TIME", "CardScanner_resumeScanning4="+System.currentTimeMillis());
        return true;
    }

    public void pauseScanning() {
        setFlashOn(false);
        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewDisplay(null);
            } catch (IOException e) {
                Log.w(TAG, "can't stop preview display", e);
            }
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mPreviewBuffer = null;
            Log.d(TAG, "- released camera");
            mCamera = null;
        }
        if (executorService != null && executorService.isShutdown()) {
        	executorService = null;
        }
        Log.i(TAG, "scan paused"); // tests look for this message. don't delete it.
    }

    public void endScanning() {
        if (mCamera != null){
            pauseScanning();
        }
        mPreviewBuffer = null;
    }

    /////////////////////////////////////////////////////////////////////////////////
    /** SurfaceHolder callbacks   */
    private boolean makePreviewGo(SurfaceHolder holder) {
        // method name from http://www.youtube.com/watch?v=-WmGvYDLsj4
        assert holder != null;
        assert holder.getSurface() != null;
        Log.d(TAG, "surfaceFrame: " + String.valueOf(holder.getSurfaceFrame()));

        if (useCamera) {
            try {
            	//connect preview to the camera!!!
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                Log.e(TAG, "can't set preview display", e);
                return false;
            }
            try {
                mCamera.startPreview();
                mCamera.autoFocus(this);
                Log.d(TAG, "startPreview success");
            } catch (RuntimeException e) {
                Log.e(TAG, "startPreview failed on camera. Error: ", e);
                return false;
            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where  to draw.
        Log.d(TAG, "Preview.surfaceCreated()");

        if (mCamera != null || !useCamera) {
            isSurfaceValid = true;
            makePreviewGo(holder);
        } else {
            Log.wtf(TAG, "CardScanner.surfaceCreated() - camera is null!");
            return;
        }
        Log.d(TAG, "Preview.surfaceCreated(), surface is valid");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Log.d(TAG, String.format("Preview.surfaceChanged(holder?:%b, f:%d, w:%d, h:%d )", (holder != null), format, width, height));
        
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Preview.surfaceDestroyed()");
        if (mCamera != null)
            try {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
            } catch (Exception e) {
                Log.e(TAG, "error stopping camera", e);
            }
        isSurfaceValid = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Handles processing of each frame.
     * 
     * This method is called by Android, never directly by application code.
     */
    private static boolean processingInProgress = false;
    private int frameSucceedReco = 0;
    private static boolean bHasResult = false;
    
    class recoThread implements Runnable {
    	private CardScanner scanner;
    	private byte[] data;
    	recoThread(CardScanner scanner, byte[] data) {
    		this.scanner = scanner;
    		this.data = data;
    	}
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
			synchronized (this) {
				Log.d(TAG, "recoThread id:"+Thread.currentThread().getId());
				processingInProgress = true;
				Rect guideRect = getGuideFrame(mFrameOrientation + ORIENTATION_OFFSET, mPreviewHeight, mPreviewWidth);
				EXBankCardInfo cardInfo = new EXBankCardInfo();
				cardInfo.charCount = 0;
				cardInfo.focusScore = 0;
				///////////////////////////////////////////////////////////////////////////////////////////////////////
				cardInfo.focusScore = EXBankCardReco.nativeFocusScore(data, mPreviewWidth, mPreviewHeight,
						previewFormat, guideRect.left, guideRect.top, guideRect.right, guideRect.bottom);
				boolean sufficientFocus = (cardInfo.focusScore >= MIN_FOCUS_SCORE);

				if (!sufficientFocus) {
					triggerAutoFocus(false);
					frameSucceedReco = 0;
				} else {
					int[] rets = new int[8];
					boolean bSucceed = false;
					frameSucceedReco++;
					// recognize it
					// ==call jni
					OverlayView.scanInstructions = "请将扫描线对准银行卡号并对齐左右边缘";

					int direction = 0;
					if (isLowResolution()) {
						direction = 1;
					} else {
						switch (mFrameOrientation) {
						case ORIENTATION_PORTRAIT:
						case ORIENTATION_PORTRAIT_UPSIDE_DOWN:
							direction = 3;
							break;
						case ORIENTATION_LANDSCAPE_LEFT:
							direction = 2;
							break;
						case ORIENTATION_LANDSCAPE_RIGHT:
							direction = 1;
							break;
						default:
							direction = 1;
							break;
						}
					}
					Bitmap cardim = EXBankCardReco.nativeRecoNV21ST(data, mPreviewWidth, mPreviewHeight, previewFormat,
							guideRect.left, guideRect.top, guideRect.right, guideRect.bottom, direction, 1, 1,
							bResultBuf, bResultBuf.length, rets);
					nResultLen = rets[0];
					Log.i(TAG, "ret:" + nResultLen + "\n" + "direction:" + direction);

					if (nResultLen > 0 && cardim != null) {
						bSucceed = EXBankCardReco.DecodeResultV2(bResultBuf, nResultLen, cardInfo);
						Log.i(TAG, "bankname  " + cardInfo.strBankName);
						Log.i(TAG, "长度" + String.valueOf(cardInfo.strBankName.length()));
						if (bSucceed) {
							// NOTE: 如果需要保存图像，请您打开并保存到指定目录
							// savetoJPEG(data, mPreviewWidth, mPreviewHeight);
							bSucceed = false;

							if (EXBankCardInfo.FILTER_BANK) {
								bSucceed = bankIsSupport(cardInfo.strBankName);
							} else {
								bSucceed = true;
							}

							if (bSucceed) {
								if (cardInfo.bitmap != null)
									cardInfo.bitmap.recycle();
								cardInfo.bitmap = cardim;
								if (cardInfo.fullImage != null)
									cardInfo.fullImage.recycle();
								cardInfo.fullImage = corpBitmap(data, mPreviewWidth, mPreviewHeight, previewFormat,
										guideRect, direction);
								if (false) {
									saveBitmap(cardInfo.fullImage);
								}
							} else {
								OverlayView.scanInstructions = "暂不支持此类银行卡，请更换！";
							}
						}
					}

					if (bSucceed) {
						bHasResult = true;
						mCardInfo = cardInfo;
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Log.i(TAG, mCardInfo.strNumbers + mCardInfo.strBankName);
								mScanActivityRef.get().onCardDetected(mCardInfo);
							}
						});
					} else if(frameSucceedReco > 6){
		    			triggerAutoFocus(false);
		    	        frameSucceedReco = 0;
		    		}
				}
				processingInProgress = false;
			}
		}
    }
    
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (data == null) { Log.w(TAG, "frame is null! skipping"); return; }

        if (processingInProgress) {
            Log.i(TAG, "processing in progress.... dropping frame");
            // return frame buffer to pool
            if (camera != null) camera.addCallbackBuffer(data);
            return;
        }      
        //first time to invalidateOverlapView, and return for update the overlap view;
        if(isFirstFrame){
        	mScanActivityRef.get().InvalidateOverlapView();
        	isFirstFrame = false;
        	if (camera != null) camera.addCallbackBuffer(data);
        	return;
        }
        //add bomber if it not focus, drop the frame, 减轻识别压力
        if(!flagFocused){
            Log.i(TAG, "processing in progress.... dropping frame");
            // return frame buffer to pool
            if (camera != null) camera.addCallbackBuffer(data);
            triggerAutoFocus(false);
            frameSucceedReco = 0;
            return;
        }
        
        if (bHasResult && executorService != null && !executorService.isShutdown()) {
        	executorService.shutdown();
        }
        
        Log.d(TAG, "mainThread id:"+Thread.currentThread().getId());
        
        if (executorService != null && !executorService.isShutdown()) {
        	executorService.execute(new recoThread(this, data));
        }
        
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // give the image buffer back to the camera, AFTER we're done reading
        // the image.
        if (camera != null){ 
        	camera.addCallbackBuffer(data);
        }
    }
    
    private boolean bankIsSupport(String bankName) {
    	boolean ret = false;
    	String[] bankNameList = new String[] { "中国银行", "中国工商银行", "中国建设银行", "中国民生银行", "招商银行", "中国农业银行",
				"中国邮政储蓄银行有限责任公司", "中信银行", "中国光大银行", "华夏银行", "广东发展银行", "平安银行股份有限公司", "兴业银行",
				"上海浦东发展银行" };

		int n = bankNameList.length;
		for (int i = 0; i < n; i++) {
			if (bankNameList[i].equals(bankName)) {
				ret = true;
				break;
			}
		}
		return ret;
    }
    
    //make the guide rect by the previewidth preview height
    Rect getGuideFrame(int orientation, int previewWidth, int previewHeight) {
        Rect r = new Rect();
        int cardh, cardw;
		int lft, top, rgt, btm;
		
		if (isLowResolution()) {
			cardh = previewHeight * 80 / 100;
			cardw = (int) (cardh * 0.63084f);
			if (previewWidth < cardw) {
				cardw = previewWidth * 90 / 100;
				cardh = (int) (cardw / 0.63084f);
			}
			lft = (previewWidth - cardw) / 2;
			top = (previewHeight - cardh) / 2;
			rgt = lft + cardw;
			btm = top + cardh;
			if (orientation > ORIENTATION_OFFSET) {
				r = new Rect(top, lft, btm, rgt);
			} else {
				r = new Rect(lft, top, rgt, btm);
			}
		} else {
			int ori = orientation;
			if (isFirstFrame) { // first time default to portrait
				ori = ORIENTATION_PORTRAIT;
			}
			switch (ori) {
			case ORIENTATION_PORTRAIT:
			case ORIENTATION_PORTRAIT_UPSIDE_DOWN:
				cardw = previewWidth * 90 / 100;
				cardh = (int) (cardw * 0.63084f);
				if (previewHeight < cardh) {
					cardh = previewHeight * 90 / 100;
					cardw = (int) (cardh / 0.63084f);
				}
				lft = (previewWidth - cardw) / 2;
				top = (previewHeight - cardh) / 2;
				rgt = lft + cardw;
				btm = top + cardh;
				r = new Rect(lft, top, rgt, btm);
				// r = new Rect(58, 593, 1021, 1200);
				break;
			case ORIENTATION_PORTRAIT+ORIENTATION_OFFSET:
			case ORIENTATION_PORTRAIT_UPSIDE_DOWN+ORIENTATION_OFFSET:
				cardw = previewWidth * 90 / 100;
				cardh = (int) (cardw * 0.63084f);
				if (previewHeight < cardh) {
					cardh = previewHeight * 90 / 100;
					cardw = (int) (cardh / 0.63084f);
				}
				lft = (previewWidth - cardw) / 2;
				top = (previewHeight - cardh) / 2;
				rgt = lft + cardw;
				btm = top + cardh;
				r = new Rect(top, lft, btm, rgt);
				// r = new Rect(58, 593, 1021, 1200);
				break;
			case ORIENTATION_LANDSCAPE_LEFT:
			case ORIENTATION_LANDSCAPE_RIGHT:
				cardh = previewHeight * 618 / 1000;
				cardw = (int) (cardh * 0.63084f);
				if (previewWidth < cardw) {
					cardw = previewWidth * 90 / 100;
					cardh = (int) (cardw / 0.63084f);
				}
				lft = (previewWidth - cardw) / 2;
				top = (previewHeight - cardh) / 2;
				rgt = lft + cardw;
				btm = top + cardh;
				r = new Rect(lft, top, rgt, btm);
				// r = new Rect(236, 415, 843, 1378);
				break;
			case ORIENTATION_LANDSCAPE_LEFT+ORIENTATION_OFFSET:
			case ORIENTATION_LANDSCAPE_RIGHT+ORIENTATION_OFFSET:
				cardh = previewHeight * 618 / 1000;
				cardw = (int) (cardh * 0.63084f);
				if (previewWidth < cardw) {
					cardw = previewWidth * 90 / 100;
					cardh = (int) (cardw / 0.63084f);
				}
				lft = (previewWidth - cardw) / 2;
				top = (previewHeight - cardh) / 2;
				rgt = lft + cardw;
				btm = top + cardh;
				r = new Rect(top, lft, btm, rgt);
				// r = new Rect(236, 415, 843, 1378);
				break;
			default:
				break;
			}
		}
        return r;
    }
    
    Rect getGuideFrame() {
        return getGuideFrame(mFrameOrientation, mPreviewHeight, mPreviewWidth);
    }
    Rect getGuideFrame(int width, int height) {
        return getGuideFrame(mFrameOrientation, width, height);
    }
    void setDeviceOrientation(int orientation) {
        mFrameOrientation = orientation;
    }
    int getDeviceOrientation() {
        return mFrameOrientation;
    }
    public boolean isLowResolution() {
    	return LOW_RESOLUTION;
    }
    // ------------------------------------------------------------------------
    // CAMERA CONTROL & CALLBACKS
    // ------------------------------------------------------------------------

    /**
     * Invoked when autoFocus is complete
     * 
     * This method is called by Android, never directly by application code.
     */
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        mAutoFocusCompletedAt = System.currentTimeMillis();
        flagFocused = success;
        if(success){
        	Log.d(TAG, "onAutoFocus success@@@@@@@@@@@@@@@@@@@@@@@@@");
//        	camera.cancelAutoFocus();
        }else{
        	Log.d(TAG, "onAutoFocus failed###########################");
        }
    }

    /**
     * True if autoFocus is in progress
     */
    boolean isAutoFocusing() {
        return mAutoFocusCompletedAt < mAutoFocusStartedAt;
    }

    void toggleFlash() {
        Log.d(TAG, "toggleFlash: currently " + (isFlashOn() ? "ON" : "OFF"));
        setFlashOn(!isFlashOn());
        Log.d(TAG, "toggleFlash - now " + (isFlashOn() ? "ON" : "OFF"));
    }

    // ------------------------------------------------------------------------
    // MISC CAMERA CONTROL
    // ------------------------------------------------------------------------
    void triggerAutoFocus(boolean isManual) {
        if (useCamera && !isAutoFocusing())
            try {
                mAutoFocusStartedAt = System.currentTimeMillis();
                mCamera.autoFocus(this);
            } catch (RuntimeException e) {
                Log.w(TAG, "could not trigger auto focus: " + e);
            }
    }

    public boolean isFlashOn() {
        if (!useCamera)
            return false;
        Camera.Parameters params = mCamera.getParameters();
        return params.getFlashMode().equals(Parameters.FLASH_MODE_TORCH);
    }

    public boolean setFlashOn(boolean b) {
        if (mCamera != null) {
            Log.d(TAG, "setFlashOn: " + b);
            try {
                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(b ? Parameters.FLASH_MODE_TORCH : Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
                return true;
            } catch (RuntimeException e) {
                Log.w(TAG, "Could not set flash mode: " + e);
            }
        }
        return false;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
	//preview image to bitmap
	public static Bitmap convertToBitmap(byte []data, int width, int height, int format, Rect rect){
		Bitmap bitmap = null;		
		if(format == ImageFormat.NV21 || format == ImageFormat.YUY2){ 
			YuvImage yuv_image = new YuvImage(data, format, width, height, null); 
			// Convert YuV to Jpeg  
            ByteArrayOutputStream output_stream = new ByteArrayOutputStream();  
            yuv_image.compressToJpeg(rect, 100, output_stream);  
            // Convert from Jpeg to Bitmap  
            bitmap = BitmapFactory.decodeByteArray(output_stream.toByteArray(), 0, output_stream.size());
		}
		return bitmap;
	}
	
    //get the bitmap int the rect
    public static Bitmap corpBitmap(byte []data, int width, int height, int format, Rect rect, int direction){    	
    	int w = rect.width();
		int h = rect.height();
		
		if(format == ImageFormat.NV21 || format == ImageFormat.YUY2){ 
			int frameSize = width*height;
			int []pixels = new int [w*h];
			byte []yuv = data;
			int yOffset = rect.top*width + rect.left;
			int uvOffset = (rect.top/2)*width + (rect.left/2)*2 + frameSize;
			int y, u, v, k;
			
			for(int i = 0; i < h; ++i){
				int outputOffset = i*w;
				for(int j = 0; j < w; ++j){
					y = (0xff & yuv[yOffset+j])-16;
					
					k = ((j>>1)<<1);
					v = (0xff & yuv[uvOffset+k])-128;
					u = (0xff & yuv[uvOffset+k+1])-128;
					
		            int y1192 = 1192 * y;
	                int r = (y1192 + 1634 * v);
	                int g = (y1192 - 833 * v - 400 * u);
	                int b = (y1192 + 2066 * u);

	                if (r < 0) r = 0; else if (r > 262143) r = 262143;
	                if (g < 0) g = 0; else if (g > 262143) g = 262143;
	                if (b < 0) b = 0; else if (b > 262143) b = 262143;
	                //0xargb ??	
	                pixels[outputOffset+j] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
				}
				yOffset += width;
				if(((rect.top+i) & 1) == 1){ uvOffset+= width; }
			}
			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
			
			if (direction == 1) {			//正向横屏，无需旋转
				return bitmap;
			}
			// 定义矩阵对象  
            Matrix matrix = new Matrix();  
            int degree = 0;
            // 缩放原图  
            matrix.postScale(1f, 1f);  
            // 旋转
           if (direction == 2) {			//反向横屏，旋转180				
				degree = 180;
			} else if (direction == 3) {	//竖屏，旋转90
				degree = 90;
			}
            matrix.postRotate(degree);  
			// 生成新bitmap
			Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			return dstbmp;
		}
		return null;
    }
	//will implement in C
	private float getFocusScore(byte []data, int width, int height)
	{
		float focusScore = 0;
		int top = height/2-height/4;
		int btm = height/2+height/4;
		int lft = width/2 -width/4;
		int rgt = width/2 +width/4;
		int ii, jj;
		int offset = 0;
		int sum = 0, sum_square = 0, sobel;
		int pixelcount = 0;
		double mean;
		for(ii = top; ii < btm; ++ii){
			offset = ii*width+lft;
			for(jj = lft; jj < rgt; ++jj, ++offset){
				sobel = (data[offset-width-1]&0xff)+(data[offset+width+1]&0xff)-(data[offset-width+1]&0xff)-(data[offset+width-1]&0xff);
				sobel = Math.abs(sobel);
				sum += sobel;
				sum_square += sobel*sobel;
			}
		}
		pixelcount = (rgt-lft)*(btm-top);
		mean = sum*1.0f/pixelcount;
		focusScore = (float)Math.sqrt(sum_square*1.0f/pixelcount-mean*mean);
		
		return focusScore; 
	}
	//save bitmap
	private void saveBitmap(Bitmap bitmap) { 
		Log.e(TAG, "保存图片"); 
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String date = sDateFormat.format(new java.util.Date());
		String tofile = Environment.getExternalStorageDirectory()+File.separator+Environment.DIRECTORY_DCIM+File.separator+date+"_"+".jpg"; 
		File file = new File(tofile);
		if (file.exists()) { 
			file.delete(); 
		} 
		try { 
			FileOutputStream out = new FileOutputStream(file); 
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out); 
			out.flush(); 
			out.close(); 
		Log.i(TAG, "已经保存"); 
		} catch (FileNotFoundException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		} catch (IOException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace();
		} 
	}
	// save to jpeg
	private void savetoJPEG(byte[] data, int width, int height) {
		int w, h;
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String date = sDateFormat.format(new java.util.Date());
		
		String tofile = Environment.getExternalStorageDirectory()+File.separator+Environment.DIRECTORY_DCIM+File.separator+date+"_"+".jpg";
		//String tofile = Environment.()+File.separator+Environment.DIRECTORY_DCIM+File.separator+date+"_"+gcount+".jpg";
		//String tofile = "/sdcard/DCIM/"+"NV21_"+ date+"_"+gcount+".jpg";
//		Rect frame = getGuideFrame(width, height);
		Rect frame = new Rect(0, 0, width, height);

		if (previewFormat == ImageFormat.NV21) {
			YuvImage img = new YuvImage(data, ImageFormat.NV21, width, height, null);
			OutputStream outStream = null;
			File file = new File(tofile);
			try {
				outStream = new FileOutputStream(file);
				img.compressToJpeg(frame, 100, outStream);
				outStream.flush();
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	void DrawRect(byte[] data, int w, int h, int lft, int top, int rgt, int btm, int ori)
	{
	    int i, j, offset;
	    int scanline = 0;
	    
		///////////////////////////////////////////////////////////////////////
	    if (ori == ORIENTATION_LANDSCAPE_RIGHT) {
	        scanline = top + (btm-top+1)*32/54;
	    } else if (ori == ORIENTATION_LANDSCAPE_LEFT){
	        scanline = top + (btm-top+1)*22/54;
	    } else if (ori == ORIENTATION_PORTRAIT || ori == ORIENTATION_PORTRAIT_UPSIDE_DOWN) {
	        scanline = lft + (rgt-lft+1)*32/54;
	    }

	    //top line
		offset = top*w;
	    for(j = lft; j < rgt; ++j) data[offset+j] = 0x00;
	    
	    //lft and right line
	    for(i = top+1; i < btm; ++i){
	        data[offset+lft] = 0x00;
	        data[offset+rgt] = 0x00;
	        if (ori == ORIENTATION_PORTRAIT || ori == ORIENTATION_PORTRAIT_UPSIDE_DOWN) {
	            data[offset+scanline] = 0x00;
	        }
	        offset += w;
	    }
	    
	    //bottom line
		offset = btm*w;
	    for(j = lft; j < rgt; ++j) data[offset+j] = 0x00;
	    
	    //scan line
	    if (ori == ORIENTATION_LANDSCAPE_RIGHT || ori == ORIENTATION_LANDSCAPE_LEFT) {
			offset = scanline*w;
	        for(j = lft; j < rgt; ++j) data[offset+j] = 0x00;
	    }
	}
}



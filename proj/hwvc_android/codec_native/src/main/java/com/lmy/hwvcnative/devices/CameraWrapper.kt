/*
 * Copyright (c) 2018-present, lmyooyo@gmail.com.
 *
 * This source code is licensed under the GPL license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.lmy.codec.media

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.Log
import com.lmy.hwvcnative.devices.CameraHelper

/**
 * Created by lmyooyo@gmail.com on 2018/3/21.
 */
class CameraWrapper(private var onFrameAvailableListener: SurfaceTexture.OnFrameAvailableListener) {
    enum class CameraIndex { BACK, FRONT }
    companion object {
        private val PREPARE = 0x1
        const val TAG = "CameraWrapper"
        fun open(onFrameAvailableListener: SurfaceTexture.OnFrameAvailableListener)
                : CameraWrapper {
            return CameraWrapper(onFrameAvailableListener)
        }
    }

    private var mCamera: Camera? = null
    private var mCameras = 0
    private var mCameraIndex: CameraIndex? = null

    init {
        mCameras = CameraHelper.getNumberOfCameras()
        openCamera(CameraIndex.BACK)
    }

    fun switchCamera(index: CameraIndex) {
        openCamera(index)
    }

    private fun openCamera(index: CameraIndex) {
        val tmp = if (index == CameraIndex.FRONT && mCameras < 2) {//如果没有前置摄像头，则强制使用后置摄像头
            CameraIndex.BACK
        } else {
            index
        }
        if (null != mCameraIndex && mCameraIndex == tmp) //如果已经打开过摄像头，并且当前已经是index，则不做改变
            return
        mCameraIndex = tmp
        GLEventPipeline.INSTANCE.queueEvent(Runnable {
            stopPreview()
            updateTexture()
            prepare()
            eglSurface.updateLocation(context)
            startPreview()
        })
    }

    private fun updateTexture() {
//        eglSurface.updateTexture()
//        eglSurface.surface!!.setOnFrameAvailableListener(onFrameAvailableListener)
    }

    private fun getCameraIndex(): Int {
        if (mCameraIndex == CameraIndex.FRONT)
            return Camera.CameraInfo.CAMERA_FACING_FRONT
        return Camera.CameraInfo.CAMERA_FACING_BACK
    }

    private fun prepare() {
        if (0 == mCameras) {
            Log.e(TAG, "Unavailable camera")
            return
        }

        val time = System.currentTimeMillis()
        mCamera = openCamera(getCameraIndex())
        Log.i(TAG, "open time: ${System.currentTimeMillis() - time}")
        if (null == mCamera) {
            Log.e(TAG, "mCamera is null!")
            return
        }
        val cameraParam = mCamera!!.parameters
        CameraHelper.setPreviewSize(cameraParam, 1280, 720)
        CameraHelper.setColorFormat(cameraParam)
        CameraHelper.setFocusMode(cameraParam)
        CameraHelper.setFps(cameraParam, 30)
        CameraHelper.setAutoExposureLock(cameraParam, false)
        CameraHelper.setSceneMode(cameraParam, Camera.Parameters.SCENE_MODE_AUTO)
        CameraHelper.setFlashMode(cameraParam, Camera.Parameters.FLASH_MODE_OFF)
        CameraHelper.setAntibanding(cameraParam, Camera.Parameters.ANTIBANDING_AUTO)
        CameraHelper.setVideoStabilization(cameraParam, true)
        val fps = IntArray(2)
        cameraParam.getPreviewFpsRange(fps)
        Log.i(TAG, "Config: Size(1280x$720)\n" +
                "Format(${cameraParam.previewFormat})\n" +
                "FocusMode(${cameraParam.focusMode})\n" +
                "Fps(${fps[0]}-${fps[1]})\n" +
                "AutoExposureLock(${cameraParam.autoExposureLock})\n" +
                "SceneMode(${cameraParam.sceneMode})\n" +
                "FlashMode(${cameraParam.flashMode})\n" +
                "Antibanding(${cameraParam.antibanding})\n" +
                "VideoStabilization(${cameraParam.videoStabilization})")
        try {
            mCamera!!.parameters = cameraParam
            Log.i(TAG, "Camera config")
        } catch (e: Exception) {
            mCamera?.release()
            Log.i(TAG, "Camera $mCameraIndex open failed. Please check parameters")
        }
    }

    private fun openCamera(index: Int): Camera? {
        return try {
            val camera = Camera.open(index)
            camera.setDisplayOrientation(90)
            camera
        } catch (e: SecurityException) {
            Log.i(TAG, "Camera $index open failed, No permission")
            e.printStackTrace()
            null
        } catch (e: Exception) {
            Log.i(TAG, "Camera $index open failed")
            e.printStackTrace()
            null
        }
    }

    fun release() {
        GLEventPipeline.INSTANCE.queueEvent(Runnable {
            stopPreview()
            releaseTexture()
        })
    }

    private fun startPreview() {
        if (null == mCamera) {
            Log.i(TAG, "Start preview failed, mCamera is null")
            return
        }
        try {
            mCamera!!.setPreviewTexture(eglSurface.surface)
            mCamera!!.startPreview()
        } catch (e: Exception) {
            release()
            Log.e(TAG, "Start preview failed")
            e.printStackTrace()
        }
    }

    private fun stopPreview() {
        if (null == mCamera) return
        try {
            mCamera!!.setPreviewTexture(null)
            mCamera!!.setPreviewCallback(null)
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        } catch (e: Exception) {
            Log.e(TAG, "Stop preview failed")
            e.printStackTrace()
        }
    }

    private fun releaseTexture() {
        eglSurface.release()
        Log.i(TAG, "releaseTexture")
    }
}
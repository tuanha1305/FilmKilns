/*
* Copyright (c) 2018-present, aliminabc@gmail.com.
*
* This source code is licensed under the MIT license found in the
* LICENSE file in the root directory of this source tree.
*/

#include <cmath>
#include "AlRotateFilter.h"
#include "Logcat.h"

#define TAG "AlRotateFilter"

AlRotateFilter::AlRotateFilter() : HwNormalFilter() {

}

AlRotateFilter::~AlRotateFilter() {

}

void AlRotateFilter::drawFirst(HwProgram *program, HwAbsTexture *src, HwAbsTexture *dest) {
    HwAbsFilter::drawFirst(program, src, dest);
    auto r = fmod(abs(rotation.toFloat()), 2.0);
    float *vertex = nullptr;
    if (0.5 == r) {
        vertex = new float[8]{
                1.0f, -1.0f,//RIGHT,BOTTOM
                1.0f, 1.0f,//RIGHT,TOP
                -1.0f, -1.0f,//LEFT,BOTTOM
                -1.0f, 1.0f,//LEFT,TOP
        };
    } else if (1.0 == r) {
        ///TODO 待验证
        vertex = new float[8]{
                1.0f, 1.0f,//RIGHT,TOP
                -1.0f, 1.0f,//LEFT,TOP
                1.0f, -1.0f,//RIGHT,BOTTOM
                -1.0f, -1.0f,//LEFT,BOTTOM
        };
    } else if (1.5 == r) {
        vertex = new float[8]{
                -1.0f, 1.0f,//LEFT,TOP
                -1.0f, -1.0f,//LEFT,BOTTOM
                1.0f, 1.0f,//RIGHT,TOP
                1.0f, -1.0f,//RIGHT,BOTTOM
        };
    } else {
        Logcat::w(TAG, "%s(%d): Not support this angle yet.", __FUNCTION__, __LINE__);
    }
    program->updateLocation(nullptr, vertex);
    delete[] vertex;
}

void AlRotateFilter::setRotation(AlRational &r) {
    this->rotation = r;
}

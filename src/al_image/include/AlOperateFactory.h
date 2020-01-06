/*
* Copyright (c) 2018-present, aliminabc@gmail.com.
*
* This source code is licensed under the MIT license found in the
* LICENSE file in the root directory of this source tree.
*/

#ifndef HWVC_ANDROID_ALOPERATEFACTORY_H
#define HWVC_ANDROID_ALOPERATEFACTORY_H

#include "AlAbsMAction.h"

al_class(AlOperateFactory) {
public:
    static AlAbsMAction *crop(float left, float top, float right, float bottom);

    static AlAbsMAction *alignCrop(AlRational &r);

private:
    AlOperateFactory() : Object() {};

    AlOperateFactory(const AlOperateFactory &o) : Object() {};

    ~AlOperateFactory() {};
};


#endif //HWVC_ANDROID_ALOPERATEFACTORY_H

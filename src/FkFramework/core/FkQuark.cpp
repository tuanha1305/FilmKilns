/*
* Copyright (c) 2018-present, aliminabc@gmail.com.
*
* This source code is licensed under the MIT license found in the
* LICENSE file in the root directory of this source tree.
*/

#include "FkQuark.h"

FkQuark::FkQuark() : FkObject() {
    prot = std::make_shared<FkProtDesc>(this);
}

FkQuark::~FkQuark() {

}

void FkQuark::onCreate() {
    describeProtocols(prot);
}

void FkQuark::onDestroy() {

}

void FkQuark::onStart() {

}

void FkQuark::onStop() {

}

FkResult FkQuark::dispatch(std::shared_ptr<FkProtocol> p) {
    auto handler = prot->find(p.get());
    if (handler) {
        return (this->*handler)(p);
    }
    return FK_FAIL;
}

FkResult FkQuark::accept(const std::shared_ptr<FkProtocol> p) {
    if (prot->find(p.get())) {
        return FK_OK;
    }
    return FK_FAIL;
}
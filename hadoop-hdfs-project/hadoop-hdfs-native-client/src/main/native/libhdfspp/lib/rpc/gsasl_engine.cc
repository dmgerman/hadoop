/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <sstream>
#include <gsasl.h>
#include  "sasl_engine.h"
#include "gsasl_engine.h"
#include "common/logging.h"

namespace hdfs {

/*****************************************************************************
 *               GSASL UTILITY FUNCTIONS
 */

static Status rc_to_status(int rc)
{
  if (rc == GSASL_OK) {
    return Status::OK();
  } else {
    std::ostringstream ss;
    ss << "Cannot initialize client (" << rc << "): " << gsasl_strerror(rc);
    return Status::Error(ss.str().c_str());
  }
}

static
std::pair<Status, std::string> base64_encode(const std::string & in) {
    char * temp;
    size_t len;
    std::string retval;
    (void)base64_encode;

    int rc = gsasl_base64_to(in.c_str(), in.size(), &temp, &len);

    if (rc != GSASL_OK) {
      return std::make_pair(rc_to_status(rc), "");
    }

    if (temp) {
        retval = temp;
        free(temp);
    }

    if (!temp || retval.length() != len) {
        return std::make_pair(Status::Error("SaslEngine: Failed to encode string to base64"), "");
    }

    return std::make_pair(Status::OK(), retval);
}

/*****************************************************************************
 *                     GSASL ENGINE
 */

GSaslEngine::~GSaslEngine()
{
  if (session_ != nullptr) {
      gsasl_finish(session_);
  }

  if (ctx_ != nullptr) {
      gsasl_done(ctx_);
  }
}

Status GSaslEngine::gsasl_new() {
   int status = GSASL_OK;

   if (ctx_) return Status::OK();

   status = gsasl_init( & ctx_);

   switch ( status) {
   case GSASL_OK:
      return Status::OK();
   case GSASL_MALLOC_ERROR:
      LOG_WARN(kRPC, <<   "GSaslEngine: Out of memory.");
      return Status::Error("SaslEngine: Out of memory.");
   default:
      LOG_WARN(kRPC, <<   "GSaslEngine: Unexpected error." << status);
      return Status::Error("SaslEngine: Unexpected error.");
   }
} // gsasl_new()

std::pair<Status, std::string>
GSaslEngine::Start()
{
  int    rc;
  Status status;

  this->gsasl_new();

  /* Create new authentication session. */
  rc = gsasl_client_start(ctx_, chosen_mech_.mechanism.c_str(), &session_);
  if (rc != GSASL_OK) {
    state_ = kErrorState;
    return std::make_pair( rc_to_status( rc), std::string(""));
  }
  init_kerberos();

  state_ = kWaitingForData;

  // get from the sasl library the initial token
  // that we'll send to the application server:
  return this->Step( chosen_mech_.challenge.c_str());
} // start() method

Status GSaslEngine::init_kerberos() {

  //TODO: check that we have a principal

  gsasl_property_set(session_, GSASL_AUTHID, principal_.value().c_str());
  gsasl_property_set(session_, GSASL_HOSTNAME,   chosen_mech_.serverid.c_str());
  gsasl_property_set(session_, GSASL_SERVICE,    chosen_mech_.protocol.c_str());
  return Status::OK();
  }

std::pair<Status, std::string> GSaslEngine::Step(const std::string data) {
  if (state_ != kWaitingForData)
    LOG_WARN(kRPC, << "GSaslEngine::step when state is " << state_);

  char * output = NULL;
  size_t outputSize;
  int rc = gsasl_step(session_, data.c_str(), data.size(), &output,
          &outputSize);

  if (rc == GSASL_NEEDS_MORE || rc == GSASL_OK) {
    std::string retval(output, output ? outputSize : 0);
    if (output) {
      free(output);
    }

    if (rc == GSASL_OK) {
      state_ = kSuccess;
    }

    return std::make_pair(Status::OK(), retval);
  }
  else {
    if (output) {
      free(output);
    }
    state_ = kFailure;
    return std::make_pair(rc_to_status(rc), "");
  }
}

Status GSaslEngine::Finish()
{
  if (state_ != kSuccess && state_ != kFailure && state_ != kErrorState )
    LOG_WARN(kRPC, << "GSaslEngine::finish when state is " << state_);

  if (session_ != nullptr) {
      gsasl_finish(session_);
      session_ = NULL;
  }

  if (ctx_ != nullptr) {
      gsasl_done(ctx_);
      ctx_ = nullptr;
  }

  return Status::OK();
} // finish() method

} // namespace hdfs

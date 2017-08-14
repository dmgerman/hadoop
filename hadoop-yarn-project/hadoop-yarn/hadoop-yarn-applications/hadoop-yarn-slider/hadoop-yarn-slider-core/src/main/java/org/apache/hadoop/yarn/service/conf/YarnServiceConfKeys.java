begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|conf
package|;
end_package

begin_interface
DECL|interface|YarnServiceConfKeys
specifier|public
interface|interface
name|YarnServiceConfKeys
block|{
comment|// Retry settings for the ServiceClient to talk to Service AppMaster
DECL|field|CLIENT_AM_RETRY_MAX_WAIT_MS
name|String
name|CLIENT_AM_RETRY_MAX_WAIT_MS
init|=
literal|"yarn.service.client-am.retry.max-wait-ms"
decl_stmt|;
DECL|field|CLIENT_AM_RETRY_MAX_INTERVAL_MS
name|String
name|CLIENT_AM_RETRY_MAX_INTERVAL_MS
init|=
literal|"yarn.service.client-am.retry-interval-ms"
decl_stmt|;
block|}
end_interface

end_unit


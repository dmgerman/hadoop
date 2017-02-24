begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|router
package|;
end_package

begin_comment
comment|/**  * The router is a stateless YARN component which is the entry point to the  * cluster. It can be deployed on multiple nodes behind a Virtual IP (VIP) with  * a LoadBalancer.  *  * The Router exposes the ApplicationClientProtocol (RPC and REST) to the  * outside world, transparently hiding the presence of ResourceManager(s), which  * allows users to request and update reservations, submit and kill  * applications, and request status on running applications.  *  * In addition, it exposes the ResourceManager Admin API.  *  * This provides a placeholder for throttling mis-behaving clients (YARN-1546)  * and masks the access to multiple RMs (YARN-3659).  */
end_comment

begin_class
DECL|class|Router
specifier|public
class|class
name|Router
block|{  }
end_class

end_unit


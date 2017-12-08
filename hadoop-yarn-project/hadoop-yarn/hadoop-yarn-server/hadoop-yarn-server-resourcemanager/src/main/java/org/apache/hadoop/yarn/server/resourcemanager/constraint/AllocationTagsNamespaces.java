begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.constraint
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
name|resourcemanager
operator|.
name|constraint
package|;
end_package

begin_comment
comment|/**  * Predefined namespaces for tags  *  * Same as namespace  of resource types. Namespaces of placement tags are start  * with alphabets and ended with "/"  */
end_comment

begin_class
DECL|class|AllocationTagsNamespaces
specifier|public
class|class
name|AllocationTagsNamespaces
block|{
DECL|field|APP_ID
specifier|public
specifier|static
specifier|final
name|String
name|APP_ID
init|=
literal|"yarn_app_id/"
decl_stmt|;
block|}
end_class

end_unit


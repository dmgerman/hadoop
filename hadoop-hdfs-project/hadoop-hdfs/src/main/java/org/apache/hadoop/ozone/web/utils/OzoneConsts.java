begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|utils
package|;
end_package

begin_comment
comment|/**  * Set of constants used in Ozone implementation.  */
end_comment

begin_class
DECL|class|OzoneConsts
specifier|public
specifier|final
class|class
name|OzoneConsts
block|{
DECL|field|OZONE_SIMPLE_ROOT_USER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SIMPLE_ROOT_USER
init|=
literal|"root"
decl_stmt|;
DECL|field|OZONE_SIMPLE_HDFS_USER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SIMPLE_HDFS_USER
init|=
literal|"hdfs"
decl_stmt|;
DECL|method|OzoneConsts ()
specifier|private
name|OzoneConsts
parameter_list|()
block|{
comment|// Never Constructed
block|}
block|}
end_class

end_unit


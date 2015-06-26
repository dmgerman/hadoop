begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * This class contains constants for configuration keys used in Ozone.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|OzoneConfigKeys
specifier|public
specifier|final
class|class
name|OzoneConfigKeys
block|{
DECL|field|DFS_STORAGE_LOCAL_ROOT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_STORAGE_LOCAL_ROOT
init|=
literal|"dfs.ozone.localstorage.root"
decl_stmt|;
DECL|field|DFS_STORAGE_LOCAL_ROOT_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_STORAGE_LOCAL_ROOT_DEFAULT
init|=
literal|"/tmp/ozone"
decl_stmt|;
comment|/**    * There is no need to instantiate this class.    */
DECL|method|OzoneConfigKeys ()
specifier|private
name|OzoneConfigKeys
parameter_list|()
block|{   }
block|}
end_class

end_unit


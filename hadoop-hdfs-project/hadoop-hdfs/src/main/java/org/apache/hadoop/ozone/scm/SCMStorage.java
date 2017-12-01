begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
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
name|conf
operator|.
name|OzoneConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|common
operator|.
name|Storage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|NodeType
import|;
end_import

begin_import
import|import
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
operator|.
name|OzoneUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * SCMStorage is responsible for management of the StorageDirectories used by  * the SCM.  */
end_comment

begin_class
DECL|class|SCMStorage
specifier|public
class|class
name|SCMStorage
extends|extends
name|Storage
block|{
DECL|field|STORAGE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_DIR
init|=
literal|"scm"
decl_stmt|;
DECL|field|SCM_ID
specifier|public
specifier|static
specifier|final
name|String
name|SCM_ID
init|=
literal|"scmUuid"
decl_stmt|;
comment|/**    * Construct SCMStorage.    * @throws IOException if any directories are inaccessible.    */
DECL|method|SCMStorage (OzoneConfiguration conf)
specifier|public
name|SCMStorage
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|NodeType
operator|.
name|SCM
argument_list|,
name|OzoneUtils
operator|.
name|getScmMetadirPath
argument_list|(
name|conf
argument_list|)
argument_list|,
name|STORAGE_DIR
argument_list|)
expr_stmt|;
block|}
DECL|method|setScmId (String scmId)
specifier|public
name|void
name|setScmId
parameter_list|(
name|String
name|scmId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|getState
argument_list|()
operator|==
name|StorageState
operator|.
name|INITIALIZED
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"SCM is already initialized."
argument_list|)
throw|;
block|}
else|else
block|{
name|getStorageInfo
argument_list|()
operator|.
name|setProperty
argument_list|(
name|SCM_ID
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Retrieves the SCM ID from the version file.    * @return SCM_ID    */
DECL|method|getScmId ()
specifier|public
name|String
name|getScmId
parameter_list|()
block|{
return|return
name|getStorageInfo
argument_list|()
operator|.
name|getProperty
argument_list|(
name|SCM_ID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeProperties ()
specifier|protected
name|Properties
name|getNodeProperties
parameter_list|()
block|{
name|String
name|scmId
init|=
name|getScmId
argument_list|()
decl_stmt|;
if|if
condition|(
name|scmId
operator|==
literal|null
condition|)
block|{
name|scmId
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|Properties
name|scmProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|scmProperties
operator|.
name|setProperty
argument_list|(
name|SCM_ID
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
return|return
name|scmProperties
return|;
block|}
block|}
end_class

end_unit


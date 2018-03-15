begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Licensed to the Apache Software Foundation (ASF) under one   * or more contributor license agreements.  See the NOTICE file   * distributed with this work for additional information   * regarding copyright ownership.  The ASF licenses this file   * to you under the Apache License, Version 2.0 (the   * "License"); you may not use this file except in compliance   * with the License.  You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * The exception is thrown when file system state is inconsistent  * and is not recoverable.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|InconsistentStorageStateException
specifier|public
class|class
name|InconsistentStorageStateException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|InconsistentStorageStateException (String descr)
specifier|public
name|InconsistentStorageStateException
parameter_list|(
name|String
name|descr
parameter_list|)
block|{
name|super
argument_list|(
name|descr
argument_list|)
expr_stmt|;
block|}
DECL|method|InconsistentStorageStateException (File dir, String descr)
specifier|public
name|InconsistentStorageStateException
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|descr
parameter_list|)
block|{
name|super
argument_list|(
literal|"Directory "
operator|+
name|getFilePath
argument_list|(
name|dir
argument_list|)
operator|+
literal|" is in an inconsistent state: "
operator|+
name|descr
argument_list|)
expr_stmt|;
block|}
DECL|method|getFilePath (File dir)
specifier|private
specifier|static
name|String
name|getFilePath
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
try|try
block|{
return|return
name|dir
operator|.
name|getCanonicalPath
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
return|return
name|dir
operator|.
name|getPath
argument_list|()
return|;
block|}
block|}
end_class

end_unit


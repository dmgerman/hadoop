begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
comment|/**  * A helper to load the native hadoop code i.e. libhadoop.so.  * This handles the fallback to either the bundled libhadoop-Linux-i386-32.so  * or the default java implementations where appropriate.  *    */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|NativeCodeLoader
specifier|public
specifier|final
class|class
name|NativeCodeLoader
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NativeCodeLoader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nativeCodeLoaded
specifier|private
specifier|static
name|boolean
name|nativeCodeLoaded
init|=
literal|false
decl_stmt|;
static|static
block|{
comment|// Try to load native hadoop library and set fallback flag appropriately
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Trying to load the custom-built native-hadoop library..."
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|System
operator|.
name|loadLibrary
argument_list|(
literal|"hadoop"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loaded the native-hadoop library"
argument_list|)
expr_stmt|;
name|nativeCodeLoaded
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Ignore failure to load
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to load native-hadoop with error: "
operator|+
name|t
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"java.library.path="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.library.path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|nativeCodeLoaded
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to load native-hadoop library for your platform... "
operator|+
literal|"using builtin-java classes where applicable"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|NativeCodeLoader ()
specifier|private
name|NativeCodeLoader
parameter_list|()
block|{}
comment|/**    * Check if native-hadoop code is loaded for this platform.    *     * @return<code>true</code> if native-hadoop is loaded,     *         else<code>false</code>    */
DECL|method|isNativeCodeLoaded ()
specifier|public
specifier|static
name|boolean
name|isNativeCodeLoaded
parameter_list|()
block|{
return|return
name|nativeCodeLoaded
return|;
block|}
comment|/**    * Returns true only if this build was compiled with support for snappy.    */
DECL|method|buildSupportsSnappy ()
specifier|public
specifier|static
specifier|native
name|boolean
name|buildSupportsSnappy
parameter_list|()
function_decl|;
comment|/**    * Returns true only if this build was compiled with support for ISA-L.    */
DECL|method|buildSupportsIsal ()
specifier|public
specifier|static
specifier|native
name|boolean
name|buildSupportsIsal
parameter_list|()
function_decl|;
comment|/**   * Returns true only if this build was compiled with support for ZStandard.    */
DECL|method|buildSupportsZstd ()
specifier|public
specifier|static
specifier|native
name|boolean
name|buildSupportsZstd
parameter_list|()
function_decl|;
comment|/**    * Returns true only if this build was compiled with support for openssl.    */
DECL|method|buildSupportsOpenssl ()
specifier|public
specifier|static
specifier|native
name|boolean
name|buildSupportsOpenssl
parameter_list|()
function_decl|;
DECL|method|getLibraryName ()
specifier|public
specifier|static
specifier|native
name|String
name|getLibraryName
parameter_list|()
function_decl|;
block|}
end_class

end_unit


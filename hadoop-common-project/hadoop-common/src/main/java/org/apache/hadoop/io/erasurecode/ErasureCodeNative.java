begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
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
name|util
operator|.
name|NativeCodeLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Erasure code native libraries (for now, Intel ISA-L) related utilities.  */
end_comment

begin_class
DECL|class|ErasureCodeNative
specifier|public
specifier|final
class|class
name|ErasureCodeNative
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ErasureCodeNative
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * The reason why ISA-L library is not available, or null if it is available.    */
DECL|field|LOADING_FAILURE_REASON
specifier|private
specifier|static
specifier|final
name|String
name|LOADING_FAILURE_REASON
decl_stmt|;
static|static
block|{
if|if
condition|(
operator|!
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
name|LOADING_FAILURE_REASON
operator|=
literal|"hadoop native library cannot be loaded."
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|NativeCodeLoader
operator|.
name|buildSupportsIsal
argument_list|()
condition|)
block|{
name|LOADING_FAILURE_REASON
operator|=
literal|"libhadoop was built without ISA-L support"
expr_stmt|;
block|}
else|else
block|{
name|String
name|problem
init|=
literal|null
decl_stmt|;
try|try
block|{
name|loadLibrary
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|problem
operator|=
literal|"Loading ISA-L failed: "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Loading ISA-L failed"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
name|LOADING_FAILURE_REASON
operator|=
name|problem
expr_stmt|;
block|}
if|if
condition|(
name|LOADING_FAILURE_REASON
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"ISA-L support is not available in your platform... "
operator|+
literal|"using builtin-java codec where applicable"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|ErasureCodeNative ()
specifier|private
name|ErasureCodeNative
parameter_list|()
block|{}
comment|/**    * Are native libraries loaded?    */
DECL|method|isNativeCodeLoaded ()
specifier|public
specifier|static
name|boolean
name|isNativeCodeLoaded
parameter_list|()
block|{
return|return
name|LOADING_FAILURE_REASON
operator|==
literal|null
return|;
block|}
comment|/**    * Is the native ISA-L library loaded and initialized? Throw exception if not.    */
DECL|method|checkNativeCodeLoaded ()
specifier|public
specifier|static
name|void
name|checkNativeCodeLoaded
parameter_list|()
block|{
if|if
condition|(
name|LOADING_FAILURE_REASON
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|LOADING_FAILURE_REASON
argument_list|)
throw|;
block|}
block|}
comment|/**    * Load native library available or supported.    */
DECL|method|loadLibrary ()
specifier|public
specifier|static
specifier|native
name|void
name|loadLibrary
parameter_list|()
function_decl|;
comment|/**    * Get the native library name that's available or supported.    */
DECL|method|getLibraryName ()
specifier|public
specifier|static
specifier|native
name|String
name|getLibraryName
parameter_list|()
function_decl|;
DECL|method|getLoadingFailureReason ()
specifier|public
specifier|static
name|String
name|getLoadingFailureReason
parameter_list|()
block|{
return|return
name|LOADING_FAILURE_REASON
return|;
block|}
block|}
end_class

end_unit


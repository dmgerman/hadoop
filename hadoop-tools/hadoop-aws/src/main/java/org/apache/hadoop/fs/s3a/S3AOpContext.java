begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|fs
operator|.
name|FileStatus
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
name|fs
operator|.
name|FileSystem
import|;
end_import

begin_comment
comment|/**  * Base class for operation context struct passed through codepaths for main  * S3AFileSystem operations.  * Anything op-specific should be moved to a subclass of this.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"visibilitymodifier"
argument_list|)
comment|// I want a struct of finals, for real.
DECL|class|S3AOpContext
specifier|public
class|class
name|S3AOpContext
block|{
DECL|field|isS3GuardEnabled
specifier|final
name|boolean
name|isS3GuardEnabled
decl_stmt|;
DECL|field|invoker
specifier|final
name|Invoker
name|invoker
decl_stmt|;
DECL|field|stats
annotation|@
name|Nullable
specifier|final
name|FileSystem
operator|.
name|Statistics
name|stats
decl_stmt|;
DECL|field|instrumentation
specifier|final
name|S3AInstrumentation
name|instrumentation
decl_stmt|;
DECL|field|s3guardInvoker
annotation|@
name|Nullable
specifier|final
name|Invoker
name|s3guardInvoker
decl_stmt|;
comment|/** FileStatus for "destination" path being operated on. */
DECL|field|dstFileStatus
specifier|protected
specifier|final
name|FileStatus
name|dstFileStatus
decl_stmt|;
comment|/**    * Alternate constructor that allows passing in two invokers, the common    * one, and another with the S3Guard Retry Policy.    * @param isS3GuardEnabled true if s3Guard is active    * @param invoker invoker, which contains retry policy    * @param s3guardInvoker s3guard-specific retry policy invoker    * @param stats optional stats object    * @param instrumentation instrumentation to use    * @param dstFileStatus file status from existence check    */
DECL|method|S3AOpContext (boolean isS3GuardEnabled, Invoker invoker, Invoker s3guardInvoker, @Nullable FileSystem.Statistics stats, S3AInstrumentation instrumentation, FileStatus dstFileStatus)
specifier|public
name|S3AOpContext
parameter_list|(
name|boolean
name|isS3GuardEnabled
parameter_list|,
name|Invoker
name|invoker
parameter_list|,
name|Invoker
name|s3guardInvoker
parameter_list|,
annotation|@
name|Nullable
name|FileSystem
operator|.
name|Statistics
name|stats
parameter_list|,
name|S3AInstrumentation
name|instrumentation
parameter_list|,
name|FileStatus
name|dstFileStatus
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|invoker
argument_list|,
literal|"Null invoker arg"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|instrumentation
argument_list|,
literal|"Null instrumentation arg"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|dstFileStatus
argument_list|,
literal|"Null dstFileStatus arg"
argument_list|)
expr_stmt|;
name|this
operator|.
name|isS3GuardEnabled
operator|=
name|isS3GuardEnabled
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|isS3GuardEnabled
operator|||
name|s3guardInvoker
operator|!=
literal|null
argument_list|,
literal|"S3Guard invoker required: S3Guard is enabled."
argument_list|)
expr_stmt|;
name|this
operator|.
name|invoker
operator|=
name|invoker
expr_stmt|;
name|this
operator|.
name|s3guardInvoker
operator|=
name|s3guardInvoker
expr_stmt|;
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|instrumentation
operator|=
name|instrumentation
expr_stmt|;
name|this
operator|.
name|dstFileStatus
operator|=
name|dstFileStatus
expr_stmt|;
block|}
comment|/**    * Constructor using common invoker and retry policy.    * @param isS3GuardEnabled true if s3Guard is active    * @param invoker invoker, which contains retry policy    * @param stats optional stats object    * @param instrumentation instrumentation to use    * @param dstFileStatus file status from existence check    */
DECL|method|S3AOpContext (boolean isS3GuardEnabled, Invoker invoker, @Nullable FileSystem.Statistics stats, S3AInstrumentation instrumentation, FileStatus dstFileStatus)
specifier|public
name|S3AOpContext
parameter_list|(
name|boolean
name|isS3GuardEnabled
parameter_list|,
name|Invoker
name|invoker
parameter_list|,
annotation|@
name|Nullable
name|FileSystem
operator|.
name|Statistics
name|stats
parameter_list|,
name|S3AInstrumentation
name|instrumentation
parameter_list|,
name|FileStatus
name|dstFileStatus
parameter_list|)
block|{
name|this
argument_list|(
name|isS3GuardEnabled
argument_list|,
name|invoker
argument_list|,
literal|null
argument_list|,
name|stats
argument_list|,
name|instrumentation
argument_list|,
name|dstFileStatus
argument_list|)
expr_stmt|;
block|}
DECL|method|isS3GuardEnabled ()
specifier|public
name|boolean
name|isS3GuardEnabled
parameter_list|()
block|{
return|return
name|isS3GuardEnabled
return|;
block|}
DECL|method|getInvoker ()
specifier|public
name|Invoker
name|getInvoker
parameter_list|()
block|{
return|return
name|invoker
return|;
block|}
annotation|@
name|Nullable
DECL|method|getStats ()
specifier|public
name|FileSystem
operator|.
name|Statistics
name|getStats
parameter_list|()
block|{
return|return
name|stats
return|;
block|}
DECL|method|getInstrumentation ()
specifier|public
name|S3AInstrumentation
name|getInstrumentation
parameter_list|()
block|{
return|return
name|instrumentation
return|;
block|}
annotation|@
name|Nullable
DECL|method|getS3guardInvoker ()
specifier|public
name|Invoker
name|getS3guardInvoker
parameter_list|()
block|{
return|return
name|s3guardInvoker
return|;
block|}
DECL|method|getDstFileStatus ()
specifier|public
name|FileStatus
name|getDstFileStatus
parameter_list|()
block|{
return|return
name|dstFileStatus
return|;
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
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
name|fs
operator|.
name|QuotaUsage
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
name|hdfs
operator|.
name|protocol
operator|.
name|DSQuotaExceededException
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
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
name|hdfs
operator|.
name|protocol
operator|.
name|NSQuotaExceededException
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|DirectoryWithQuotaFeature
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|Quota
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * The subclass of {@link QuotaUsage} used in Router-based federation.  */
end_comment

begin_class
DECL|class|RouterQuotaUsage
specifier|public
specifier|final
class|class
name|RouterQuotaUsage
extends|extends
name|QuotaUsage
block|{
comment|/** Default quota usage count. */
DECL|field|QUOTA_USAGE_COUNT_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|QUOTA_USAGE_COUNT_DEFAULT
init|=
literal|0
decl_stmt|;
DECL|method|RouterQuotaUsage (Builder builder)
specifier|private
name|RouterQuotaUsage
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
comment|/** Build the instance based on the builder. */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|QuotaUsage
operator|.
name|Builder
block|{
DECL|method|build ()
specifier|public
name|RouterQuotaUsage
name|build
parameter_list|()
block|{
return|return
operator|new
name|RouterQuotaUsage
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fileAndDirectoryCount (long count)
specifier|public
name|Builder
name|fileAndDirectoryCount
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|super
operator|.
name|fileAndDirectoryCount
argument_list|(
name|count
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|quota (long quota)
specifier|public
name|Builder
name|quota
parameter_list|(
name|long
name|quota
parameter_list|)
block|{
name|super
operator|.
name|quota
argument_list|(
name|quota
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|spaceConsumed (long spaceConsumed)
specifier|public
name|Builder
name|spaceConsumed
parameter_list|(
name|long
name|spaceConsumed
parameter_list|)
block|{
name|super
operator|.
name|spaceConsumed
argument_list|(
name|spaceConsumed
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|spaceQuota (long spaceQuota)
specifier|public
name|Builder
name|spaceQuota
parameter_list|(
name|long
name|spaceQuota
parameter_list|)
block|{
name|super
operator|.
name|spaceQuota
argument_list|(
name|spaceQuota
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
comment|/**    * Verify if namespace quota is violated once quota is set. Relevant    * method {@link DirectoryWithQuotaFeature#verifyNamespaceQuota}.    * @throws NSQuotaExceededException If the quota is exceeded.    */
DECL|method|verifyNamespaceQuota ()
specifier|public
name|void
name|verifyNamespaceQuota
parameter_list|()
throws|throws
name|NSQuotaExceededException
block|{
if|if
condition|(
name|Quota
operator|.
name|isViolated
argument_list|(
name|getQuota
argument_list|()
argument_list|,
name|getFileAndDirectoryCount
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NSQuotaExceededException
argument_list|(
name|getQuota
argument_list|()
argument_list|,
name|getFileAndDirectoryCount
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Verify if storage space quota is violated once quota is set. Relevant    * method {@link DirectoryWithQuotaFeature#verifyStoragespaceQuota}.    * @throws DSQuotaExceededException If the quota is exceeded.    */
DECL|method|verifyStoragespaceQuota ()
specifier|public
name|void
name|verifyStoragespaceQuota
parameter_list|()
throws|throws
name|DSQuotaExceededException
block|{
if|if
condition|(
name|Quota
operator|.
name|isViolated
argument_list|(
name|getSpaceQuota
argument_list|()
argument_list|,
name|getSpaceConsumed
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DSQuotaExceededException
argument_list|(
name|getSpaceQuota
argument_list|()
argument_list|,
name|getSpaceConsumed
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|nsQuota
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|getQuota
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|nsCount
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|getFileAndDirectoryCount
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|getQuota
argument_list|()
operator|==
name|HdfsConstants
operator|.
name|QUOTA_RESET
condition|)
block|{
name|nsQuota
operator|=
literal|"-"
expr_stmt|;
name|nsCount
operator|=
literal|"-"
expr_stmt|;
block|}
name|String
name|ssQuota
init|=
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|getSpaceQuota
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|ssCount
init|=
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|getSpaceConsumed
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|getSpaceQuota
argument_list|()
operator|==
name|HdfsConstants
operator|.
name|QUOTA_RESET
condition|)
block|{
name|ssQuota
operator|=
literal|"-"
expr_stmt|;
name|ssCount
operator|=
literal|"-"
expr_stmt|;
block|}
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|"[NsQuota: "
argument_list|)
operator|.
name|append
argument_list|(
name|nsQuota
argument_list|)
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|nsCount
argument_list|)
expr_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|", SsQuota: "
argument_list|)
operator|.
name|append
argument_list|(
name|ssQuota
argument_list|)
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|ssCount
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|str
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit


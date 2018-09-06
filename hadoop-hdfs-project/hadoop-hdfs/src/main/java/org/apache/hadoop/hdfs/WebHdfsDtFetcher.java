begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
name|web
operator|.
name|WebHdfsConstants
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
name|io
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  *  DtFetcher for WebHdfsFileSystem using the base class HdfsDtFetcher impl.  */
end_comment

begin_class
DECL|class|WebHdfsDtFetcher
specifier|public
class|class
name|WebHdfsDtFetcher
extends|extends
name|HdfsDtFetcher
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
name|WebHdfsDtFetcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SERVICE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SERVICE_NAME
init|=
name|WebHdfsConstants
operator|.
name|WEBHDFS_SCHEME
decl_stmt|;
annotation|@
name|Override
DECL|method|getServiceName ()
specifier|public
name|Text
name|getServiceName
parameter_list|()
block|{
return|return
operator|new
name|Text
argument_list|(
name|SERVICE_NAME
argument_list|)
return|;
block|}
block|}
end_class

end_unit


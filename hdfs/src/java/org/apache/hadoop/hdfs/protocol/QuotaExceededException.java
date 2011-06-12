begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

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
comment|/**   * This exception is thrown when modification to HDFS results in violation  * of a directory quota. A directory quota might be namespace quota (limit   * on number of files and directories) or a diskspace quota (limit on space   * taken by all the file under the directory tree).<br><br>  *   * The message for the exception specifies the directory where the quota  * was violated and actual quotas. Specific message is generated in the   * corresponding Exception class:   *  DSQuotaExceededException or  *  NSQuotaExceededException  */
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
DECL|class|QuotaExceededException
specifier|public
class|class
name|QuotaExceededException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|protected
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|pathName
specifier|protected
name|String
name|pathName
init|=
literal|null
decl_stmt|;
DECL|field|quota
specifier|protected
name|long
name|quota
decl_stmt|;
comment|// quota
DECL|field|count
specifier|protected
name|long
name|count
decl_stmt|;
comment|// actual value
DECL|method|QuotaExceededException ()
specifier|protected
name|QuotaExceededException
parameter_list|()
block|{}
DECL|method|QuotaExceededException (String msg)
specifier|protected
name|QuotaExceededException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|QuotaExceededException (long quota, long count)
specifier|protected
name|QuotaExceededException
parameter_list|(
name|long
name|quota
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|this
operator|.
name|quota
operator|=
name|quota
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
DECL|method|setPathName (String path)
specifier|public
name|void
name|setPathName
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|pathName
operator|=
name|path
expr_stmt|;
block|}
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|super
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
end_class

end_unit


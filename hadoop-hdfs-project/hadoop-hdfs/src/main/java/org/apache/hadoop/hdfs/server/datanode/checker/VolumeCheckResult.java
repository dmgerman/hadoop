begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.checker
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
name|datanode
operator|.
name|checker
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
comment|/**  * Defines the outcomes of running a disk check operation against a  * volume.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|enum|VolumeCheckResult
specifier|public
enum|enum
name|VolumeCheckResult
block|{
DECL|enumConstant|HEALTHY
name|HEALTHY
argument_list|(
literal|1
argument_list|)
block|,
DECL|enumConstant|DEGRADED
name|DEGRADED
argument_list|(
literal|2
argument_list|)
block|,
DECL|enumConstant|FAILED
name|FAILED
argument_list|(
literal|3
argument_list|)
block|;
DECL|field|value
specifier|private
specifier|final
name|int
name|value
decl_stmt|;
DECL|method|VolumeCheckResult (int value)
name|VolumeCheckResult
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
end_enum

end_unit


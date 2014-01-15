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
name|util
operator|.
name|Date
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
comment|/**  * Rolling upgrade information  */
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
DECL|class|RollingUpgradeInfo
specifier|public
class|class
name|RollingUpgradeInfo
block|{
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|finalizeTime
specifier|private
name|long
name|finalizeTime
decl_stmt|;
DECL|method|RollingUpgradeInfo (long startTime, long finalizeTime)
specifier|public
name|RollingUpgradeInfo
parameter_list|(
name|long
name|startTime
parameter_list|,
name|long
name|finalizeTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
name|this
operator|.
name|finalizeTime
operator|=
name|finalizeTime
expr_stmt|;
block|}
DECL|method|isStarted ()
specifier|public
name|boolean
name|isStarted
parameter_list|()
block|{
return|return
name|startTime
operator|!=
literal|0
return|;
block|}
comment|/** @return The rolling upgrade starting time. */
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
DECL|method|isFinalized ()
specifier|public
name|boolean
name|isFinalized
parameter_list|()
block|{
return|return
name|finalizeTime
operator|!=
literal|0
return|;
block|}
DECL|method|getFinalizeTime ()
specifier|public
name|long
name|getFinalizeTime
parameter_list|()
block|{
return|return
name|finalizeTime
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"     Start Time: "
operator|+
operator|(
name|startTime
operator|==
literal|0
condition|?
literal|"<NOT STARTED>"
else|:
name|timestamp2String
argument_list|(
name|startTime
argument_list|)
operator|)
operator|+
literal|"\n  Finalize Time: "
operator|+
operator|(
name|finalizeTime
operator|==
literal|0
condition|?
literal|"<NOT FINALIZED>"
else|:
name|timestamp2String
argument_list|(
name|finalizeTime
argument_list|)
operator|)
return|;
block|}
DECL|method|timestamp2String (long timestamp)
specifier|private
specifier|static
name|String
name|timestamp2String
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
return|return
operator|new
name|Date
argument_list|(
name|timestamp
argument_list|)
operator|+
literal|" (="
operator|+
name|timestamp
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit


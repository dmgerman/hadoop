begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
name|hdfs
operator|.
name|util
operator|.
name|EnumCounters
import|;
end_import

begin_comment
comment|/** Quota types. */
end_comment

begin_enum
DECL|enum|Quota
specifier|public
enum|enum
name|Quota
block|{
comment|/** The namespace usage, i.e. the number of name objects. */
DECL|enumConstant|NAMESPACE
name|NAMESPACE
block|,
comment|/** The diskspace usage in bytes including replication. */
DECL|enumConstant|DISKSPACE
name|DISKSPACE
block|;
comment|/** Counters for quota counts. */
DECL|class|Counts
specifier|public
specifier|static
class|class
name|Counts
extends|extends
name|EnumCounters
argument_list|<
name|Quota
argument_list|>
block|{
comment|/** @return a new counter with the given namespace and diskspace usages. */
DECL|method|newInstance (long namespace, long diskspace)
specifier|public
specifier|static
name|Counts
name|newInstance
parameter_list|(
name|long
name|namespace
parameter_list|,
name|long
name|diskspace
parameter_list|)
block|{
specifier|final
name|Counts
name|c
init|=
operator|new
name|Counts
argument_list|()
decl_stmt|;
name|c
operator|.
name|set
argument_list|(
name|NAMESPACE
argument_list|,
name|namespace
argument_list|)
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
name|DISKSPACE
argument_list|,
name|diskspace
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|method|newInstance ()
specifier|public
specifier|static
name|Counts
name|newInstance
parameter_list|()
block|{
return|return
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|Counts ()
name|Counts
parameter_list|()
block|{
name|super
argument_list|(
name|Quota
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Is quota violated?    * The quota is violated if quota is set and usage> quota.     */
DECL|method|isViolated (final long quota, final long usage)
specifier|static
name|boolean
name|isViolated
parameter_list|(
specifier|final
name|long
name|quota
parameter_list|,
specifier|final
name|long
name|usage
parameter_list|)
block|{
return|return
name|quota
operator|>=
literal|0
operator|&&
name|usage
operator|>
name|quota
return|;
block|}
comment|/**    * Is quota violated?    * The quota is violated if quota is set, delta> 0 and usage + delta> quota.    */
DECL|method|isViolated (final long quota, final long usage, final long delta)
specifier|static
name|boolean
name|isViolated
parameter_list|(
specifier|final
name|long
name|quota
parameter_list|,
specifier|final
name|long
name|usage
parameter_list|,
specifier|final
name|long
name|delta
parameter_list|)
block|{
return|return
name|quota
operator|>=
literal|0
operator|&&
name|delta
operator|>
literal|0
operator|&&
name|usage
operator|>
name|quota
operator|-
name|delta
return|;
block|}
block|}
end_enum

end_unit


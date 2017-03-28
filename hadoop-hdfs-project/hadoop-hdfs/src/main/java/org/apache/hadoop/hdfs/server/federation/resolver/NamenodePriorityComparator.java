begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.resolver
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
name|resolver
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * Compares NNs in the same namespace and prioritizes by their status. The  * priorities are:  *<ul>  *<li>ACTIVE  *<li>STANDBY  *<li>UNAVAILABLE  *</ul>  * When two NNs have the same state, the last modification date is the tie  * breaker, newest has priority. Expired NNs are excluded.  */
end_comment

begin_class
DECL|class|NamenodePriorityComparator
specifier|public
class|class
name|NamenodePriorityComparator
implements|implements
name|Comparator
argument_list|<
name|FederationNamenodeContext
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (FederationNamenodeContext o1, FederationNamenodeContext o2)
specifier|public
name|int
name|compare
parameter_list|(
name|FederationNamenodeContext
name|o1
parameter_list|,
name|FederationNamenodeContext
name|o2
parameter_list|)
block|{
name|FederationNamenodeServiceState
name|state1
init|=
name|o1
operator|.
name|getState
argument_list|()
decl_stmt|;
name|FederationNamenodeServiceState
name|state2
init|=
name|o2
operator|.
name|getState
argument_list|()
decl_stmt|;
if|if
condition|(
name|state1
operator|==
name|state2
condition|)
block|{
comment|// Both have the same state, use mode dates
return|return
name|compareModDates
argument_list|(
name|o1
argument_list|,
name|o2
argument_list|)
return|;
block|}
else|else
block|{
comment|// Enum is ordered by priority
return|return
name|state1
operator|.
name|compareTo
argument_list|(
name|state2
argument_list|)
return|;
block|}
block|}
comment|/**    * Compare the modification dates.    *    * @param o1 Context 1.    * @param o2 Context 2.    * @return Comparison between dates.    */
DECL|method|compareModDates (FederationNamenodeContext o1, FederationNamenodeContext o2)
specifier|private
name|int
name|compareModDates
parameter_list|(
name|FederationNamenodeContext
name|o1
parameter_list|,
name|FederationNamenodeContext
name|o2
parameter_list|)
block|{
comment|// Reverse sort, lowest position is highest priority.
return|return
call|(
name|int
call|)
argument_list|(
name|o2
operator|.
name|getDateModified
argument_list|()
operator|-
name|o1
operator|.
name|getDateModified
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit


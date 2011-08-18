begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.index.example
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|index
operator|.
name|example
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
name|contrib
operator|.
name|index
operator|.
name|mapred
operator|.
name|DocumentID
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
name|contrib
operator|.
name|index
operator|.
name|mapred
operator|.
name|IDistributionPolicy
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
name|contrib
operator|.
name|index
operator|.
name|mapred
operator|.
name|Shard
import|;
end_import

begin_comment
comment|/**  * Choose a shard for each insert in a round-robin fashion. Choose all the  * shards for each delete because we don't know where it is stored.  */
end_comment

begin_class
DECL|class|RoundRobinDistributionPolicy
specifier|public
class|class
name|RoundRobinDistributionPolicy
implements|implements
name|IDistributionPolicy
block|{
DECL|field|numShards
specifier|private
name|int
name|numShards
decl_stmt|;
DECL|field|rr
specifier|private
name|int
name|rr
decl_stmt|;
comment|// round-robin implementation
comment|/* (non-Javadoc)    * @see org.apache.hadoop.contrib.index.mapred.IDistributionPolicy#init(org.apache.hadoop.contrib.index.mapred.Shard[])    */
DECL|method|init (Shard[] shards)
specifier|public
name|void
name|init
parameter_list|(
name|Shard
index|[]
name|shards
parameter_list|)
block|{
name|numShards
operator|=
name|shards
operator|.
name|length
expr_stmt|;
name|rr
operator|=
literal|0
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.hadoop.contrib.index.mapred.IDistributionPolicy#chooseShardForInsert(org.apache.hadoop.contrib.index.mapred.DocumentID)    */
DECL|method|chooseShardForInsert (DocumentID key)
specifier|public
name|int
name|chooseShardForInsert
parameter_list|(
name|DocumentID
name|key
parameter_list|)
block|{
name|int
name|chosen
init|=
name|rr
decl_stmt|;
name|rr
operator|=
operator|(
name|rr
operator|+
literal|1
operator|)
operator|%
name|numShards
expr_stmt|;
return|return
name|chosen
return|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.hadoop.contrib.index.mapred.IDistributionPolicy#chooseShardForDelete(org.apache.hadoop.contrib.index.mapred.DocumentID)    */
DECL|method|chooseShardForDelete (DocumentID key)
specifier|public
name|int
name|chooseShardForDelete
parameter_list|(
name|DocumentID
name|key
parameter_list|)
block|{
comment|// -1 represents all the shards
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.records
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
name|store
operator|.
name|records
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Encapsulates a state store query result that includes a set of records and a  * time stamp for the result.  */
end_comment

begin_class
DECL|class|QueryResult
specifier|public
class|class
name|QueryResult
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
block|{
comment|/** Data result. */
DECL|field|records
specifier|private
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|records
decl_stmt|;
comment|/** Time stamp of the data results. */
DECL|field|timestamp
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
DECL|method|QueryResult (final List<T> recs, final long time)
specifier|public
name|QueryResult
parameter_list|(
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|recs
parameter_list|,
specifier|final
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|records
operator|=
name|recs
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|time
expr_stmt|;
block|}
comment|/**    * Get the result of the query.    *    * @return List of records.    */
DECL|method|getRecords ()
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|getRecords
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|this
operator|.
name|records
argument_list|)
return|;
block|}
comment|/**    * The timetamp in driver time of this query.    *    * @return Timestamp in driver time.    */
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|this
operator|.
name|timestamp
return|;
block|}
block|}
end_class

end_unit


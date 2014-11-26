begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.inotify
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|inotify
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

begin_comment
comment|/**  * A batch of events that all happened on the same transaction ID.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|EventBatch
specifier|public
class|class
name|EventBatch
block|{
DECL|field|txid
specifier|private
specifier|final
name|long
name|txid
decl_stmt|;
DECL|field|events
specifier|private
specifier|final
name|Event
index|[]
name|events
decl_stmt|;
DECL|method|EventBatch (long txid, Event[] events)
specifier|public
name|EventBatch
parameter_list|(
name|long
name|txid
parameter_list|,
name|Event
index|[]
name|events
parameter_list|)
block|{
name|this
operator|.
name|txid
operator|=
name|txid
expr_stmt|;
name|this
operator|.
name|events
operator|=
name|events
expr_stmt|;
block|}
DECL|method|getTxid ()
specifier|public
name|long
name|getTxid
parameter_list|()
block|{
return|return
name|txid
return|;
block|}
DECL|method|getEvents ()
specifier|public
name|Event
index|[]
name|getEvents
parameter_list|()
block|{
return|return
name|events
return|;
block|}
block|}
end_class

end_unit


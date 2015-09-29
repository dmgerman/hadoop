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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MissingEventsException
specifier|public
class|class
name|MissingEventsException
extends|extends
name|Exception
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|expectedTxid
specifier|private
name|long
name|expectedTxid
decl_stmt|;
DECL|field|actualTxid
specifier|private
name|long
name|actualTxid
decl_stmt|;
DECL|method|MissingEventsException ()
specifier|public
name|MissingEventsException
parameter_list|()
block|{}
DECL|method|MissingEventsException (long expectedTxid, long actualTxid)
specifier|public
name|MissingEventsException
parameter_list|(
name|long
name|expectedTxid
parameter_list|,
name|long
name|actualTxid
parameter_list|)
block|{
name|this
operator|.
name|expectedTxid
operator|=
name|expectedTxid
expr_stmt|;
name|this
operator|.
name|actualTxid
operator|=
name|actualTxid
expr_stmt|;
block|}
DECL|method|getExpectedTxid ()
specifier|public
name|long
name|getExpectedTxid
parameter_list|()
block|{
return|return
name|expectedTxid
return|;
block|}
DECL|method|getActualTxid ()
specifier|public
name|long
name|getActualTxid
parameter_list|()
block|{
return|return
name|actualTxid
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
literal|"We expected the next batch of events to start with transaction ID "
operator|+
name|expectedTxid
operator|+
literal|", but it instead started with transaction ID "
operator|+
name|actualTxid
operator|+
literal|". Most likely the intervening transactions were cleaned "
operator|+
literal|"up as part of checkpointing."
return|;
block|}
block|}
end_class

end_unit


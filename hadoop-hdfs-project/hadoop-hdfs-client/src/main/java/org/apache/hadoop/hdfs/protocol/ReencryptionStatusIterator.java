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
name|fs
operator|.
name|BatchedRemoteIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|core
operator|.
name|TraceScope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|core
operator|.
name|Tracer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * ReencryptionStatusIterator is a remote iterator that iterates over the  * reencryption status of encryption zones.  * It supports retrying in case of namenode failover.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ReencryptionStatusIterator
specifier|public
class|class
name|ReencryptionStatusIterator
extends|extends
name|BatchedRemoteIterator
argument_list|<
name|Long
argument_list|,
name|ZoneReencryptionStatus
argument_list|>
block|{
DECL|field|namenode
specifier|private
specifier|final
name|ClientProtocol
name|namenode
decl_stmt|;
DECL|field|tracer
specifier|private
specifier|final
name|Tracer
name|tracer
decl_stmt|;
DECL|method|ReencryptionStatusIterator (ClientProtocol namenode, Tracer tracer)
specifier|public
name|ReencryptionStatusIterator
parameter_list|(
name|ClientProtocol
name|namenode
parameter_list|,
name|Tracer
name|tracer
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|namenode
operator|=
name|namenode
expr_stmt|;
name|this
operator|.
name|tracer
operator|=
name|tracer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|makeRequest (Long prevId)
specifier|public
name|BatchedEntries
argument_list|<
name|ZoneReencryptionStatus
argument_list|>
name|makeRequest
parameter_list|(
name|Long
name|prevId
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|TraceScope
name|ignored
init|=
name|tracer
operator|.
name|newScope
argument_list|(
literal|"listReencryptionStatus"
argument_list|)
init|)
block|{
return|return
name|namenode
operator|.
name|listReencryptionStatus
argument_list|(
name|prevId
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|elementToPrevKey (ZoneReencryptionStatus entry)
specifier|public
name|Long
name|elementToPrevKey
parameter_list|(
name|ZoneReencryptionStatus
name|entry
parameter_list|)
block|{
return|return
name|entry
operator|.
name|getId
argument_list|()
return|;
block|}
block|}
end_class

end_unit


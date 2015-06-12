begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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

begin_comment
comment|/**  * The context of the block report.  *  * This is a set of fields that the Datanode sends to provide context about a  * block report RPC.  The context includes a unique 64-bit ID which  * identifies the block report as a whole.  It also includes the total number  * of RPCs which this block report is split into, and the index into that  * total for the current RPC.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockReportContext
specifier|public
class|class
name|BlockReportContext
block|{
comment|/**    * The total number of RPCs contained in the block report.    */
DECL|field|totalRpcs
specifier|private
specifier|final
name|int
name|totalRpcs
decl_stmt|;
comment|/**    * The index of this particular RPC.    */
DECL|field|curRpc
specifier|private
specifier|final
name|int
name|curRpc
decl_stmt|;
comment|/**    * A 64-bit ID which identifies the block report as a whole.    */
DECL|field|reportId
specifier|private
specifier|final
name|long
name|reportId
decl_stmt|;
comment|/**    * The lease ID which this block report is using, or 0 if this block report is    * bypassing rate-limiting.    */
DECL|field|leaseId
specifier|private
specifier|final
name|long
name|leaseId
decl_stmt|;
DECL|method|BlockReportContext (int totalRpcs, int curRpc, long reportId, long leaseId)
specifier|public
name|BlockReportContext
parameter_list|(
name|int
name|totalRpcs
parameter_list|,
name|int
name|curRpc
parameter_list|,
name|long
name|reportId
parameter_list|,
name|long
name|leaseId
parameter_list|)
block|{
name|this
operator|.
name|totalRpcs
operator|=
name|totalRpcs
expr_stmt|;
name|this
operator|.
name|curRpc
operator|=
name|curRpc
expr_stmt|;
name|this
operator|.
name|reportId
operator|=
name|reportId
expr_stmt|;
name|this
operator|.
name|leaseId
operator|=
name|leaseId
expr_stmt|;
block|}
DECL|method|getTotalRpcs ()
specifier|public
name|int
name|getTotalRpcs
parameter_list|()
block|{
return|return
name|totalRpcs
return|;
block|}
DECL|method|getCurRpc ()
specifier|public
name|int
name|getCurRpc
parameter_list|()
block|{
return|return
name|curRpc
return|;
block|}
DECL|method|getReportId ()
specifier|public
name|long
name|getReportId
parameter_list|()
block|{
return|return
name|reportId
return|;
block|}
DECL|method|getLeaseId ()
specifier|public
name|long
name|getLeaseId
parameter_list|()
block|{
return|return
name|leaseId
return|;
block|}
block|}
end_class

end_unit


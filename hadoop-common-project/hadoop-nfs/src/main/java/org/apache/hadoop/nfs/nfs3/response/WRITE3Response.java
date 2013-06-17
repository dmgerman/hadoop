begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.nfs.nfs3.response
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|nfs
operator|.
name|nfs3
operator|.
name|response
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Constant
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Status
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Constant
operator|.
name|WriteStableHow
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
name|oncrpc
operator|.
name|XDR
import|;
end_import

begin_comment
comment|/**  * WRITE3 Response  */
end_comment

begin_class
DECL|class|WRITE3Response
specifier|public
class|class
name|WRITE3Response
extends|extends
name|NFS3Response
block|{
DECL|field|fileWcc
specifier|private
specifier|final
name|WccData
name|fileWcc
decl_stmt|;
comment|// return on both success and failure
DECL|field|count
specifier|private
specifier|final
name|int
name|count
decl_stmt|;
DECL|field|stableHow
specifier|private
specifier|final
name|WriteStableHow
name|stableHow
decl_stmt|;
DECL|field|verifer
specifier|private
specifier|final
name|long
name|verifer
decl_stmt|;
DECL|method|WRITE3Response (int status)
specifier|public
name|WRITE3Response
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
argument_list|(
name|status
argument_list|,
operator|new
name|WccData
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|0
argument_list|,
name|WriteStableHow
operator|.
name|UNSTABLE
argument_list|,
name|Nfs3Constant
operator|.
name|WRITE_COMMIT_VERF
argument_list|)
expr_stmt|;
block|}
DECL|method|WRITE3Response (int status, WccData fileWcc, int count, WriteStableHow stableHow, long verifier)
specifier|public
name|WRITE3Response
parameter_list|(
name|int
name|status
parameter_list|,
name|WccData
name|fileWcc
parameter_list|,
name|int
name|count
parameter_list|,
name|WriteStableHow
name|stableHow
parameter_list|,
name|long
name|verifier
parameter_list|)
block|{
name|super
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|this
operator|.
name|fileWcc
operator|=
name|fileWcc
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|stableHow
operator|=
name|stableHow
expr_stmt|;
name|this
operator|.
name|verifer
operator|=
name|verifier
expr_stmt|;
block|}
DECL|method|getCount ()
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
DECL|method|getStableHow ()
specifier|public
name|WriteStableHow
name|getStableHow
parameter_list|()
block|{
return|return
name|stableHow
return|;
block|}
DECL|method|getVerifer ()
specifier|public
name|long
name|getVerifer
parameter_list|()
block|{
return|return
name|verifer
return|;
block|}
annotation|@
name|Override
DECL|method|send (XDR out, int xid)
specifier|public
name|XDR
name|send
parameter_list|(
name|XDR
name|out
parameter_list|,
name|int
name|xid
parameter_list|)
block|{
name|super
operator|.
name|send
argument_list|(
name|out
argument_list|,
name|xid
argument_list|)
expr_stmt|;
name|fileWcc
operator|.
name|serialize
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|getStatus
argument_list|()
operator|==
name|Nfs3Status
operator|.
name|NFS3_OK
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|stableHow
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLongAsHyper
argument_list|(
name|verifer
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
block|}
end_class

end_unit


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
name|oncrpc
operator|.
name|XDR
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
name|security
operator|.
name|Verifier
import|;
end_import

begin_comment
comment|/**  * COMMIT3 Response  */
end_comment

begin_class
DECL|class|COMMIT3Response
specifier|public
class|class
name|COMMIT3Response
extends|extends
name|NFS3Response
block|{
DECL|field|fileWcc
specifier|private
specifier|final
name|WccData
name|fileWcc
decl_stmt|;
DECL|field|verf
specifier|private
specifier|final
name|long
name|verf
decl_stmt|;
DECL|method|COMMIT3Response (int status)
specifier|public
name|COMMIT3Response
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
name|Nfs3Constant
operator|.
name|WRITE_COMMIT_VERF
argument_list|)
expr_stmt|;
block|}
DECL|method|COMMIT3Response (int status, WccData fileWcc, long verf)
specifier|public
name|COMMIT3Response
parameter_list|(
name|int
name|status
parameter_list|,
name|WccData
name|fileWcc
parameter_list|,
name|long
name|verf
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
name|verf
operator|=
name|verf
expr_stmt|;
block|}
DECL|method|getFileWcc ()
specifier|public
name|WccData
name|getFileWcc
parameter_list|()
block|{
return|return
name|fileWcc
return|;
block|}
DECL|method|getVerf ()
specifier|public
name|long
name|getVerf
parameter_list|()
block|{
return|return
name|verf
return|;
block|}
annotation|@
name|Override
DECL|method|writeHeaderAndResponse (XDR out, int xid, Verifier verifier)
specifier|public
name|XDR
name|writeHeaderAndResponse
parameter_list|(
name|XDR
name|out
parameter_list|,
name|int
name|xid
parameter_list|,
name|Verifier
name|verifier
parameter_list|)
block|{
name|super
operator|.
name|writeHeaderAndResponse
argument_list|(
name|out
argument_list|,
name|xid
argument_list|,
name|verifier
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
name|writeLongAsHyper
argument_list|(
name|verf
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


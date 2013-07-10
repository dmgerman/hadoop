begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.portmap
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|portmap
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
name|oncrpc
operator|.
name|XDR
import|;
end_import

begin_comment
comment|/**  * Methods that need to be implemented to provide Portmap RPC program.  * See RFC 1833 for details.  */
end_comment

begin_interface
DECL|interface|PortmapInterface
specifier|public
interface|interface
name|PortmapInterface
block|{
DECL|enum|Procedure
specifier|public
enum|enum
name|Procedure
block|{
comment|// the order of the values below are significant.
DECL|enumConstant|PMAPPROC_NULL
name|PMAPPROC_NULL
block|,
DECL|enumConstant|PMAPPROC_SET
name|PMAPPROC_SET
block|,
DECL|enumConstant|PMAPPROC_UNSET
name|PMAPPROC_UNSET
block|,
DECL|enumConstant|PMAPPROC_GETPORT
name|PMAPPROC_GETPORT
block|,
DECL|enumConstant|PMAPPROC_DUMP
name|PMAPPROC_DUMP
block|,
DECL|enumConstant|PMAPPROC_CALLIT
name|PMAPPROC_CALLIT
block|,
DECL|enumConstant|PMAPPROC_GETTIME
name|PMAPPROC_GETTIME
block|,
DECL|enumConstant|PMAPPROC_UADDR2TADDR
name|PMAPPROC_UADDR2TADDR
block|,
DECL|enumConstant|PMAPPROC_TADDR2UADDR
name|PMAPPROC_TADDR2UADDR
block|,
DECL|enumConstant|PMAPPROC_GETVERSADDR
name|PMAPPROC_GETVERSADDR
block|,
DECL|enumConstant|PMAPPROC_INDIRECT
name|PMAPPROC_INDIRECT
block|,
DECL|enumConstant|PMAPPROC_GETADDRLIST
name|PMAPPROC_GETADDRLIST
block|,
DECL|enumConstant|PMAPPROC_GETSTAT
name|PMAPPROC_GETSTAT
block|;
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|ordinal
argument_list|()
return|;
block|}
DECL|method|fromValue (int value)
specifier|public
specifier|static
name|Procedure
name|fromValue
parameter_list|(
name|int
name|value
parameter_list|)
block|{
return|return
name|values
argument_list|()
index|[
name|value
index|]
return|;
block|}
block|}
comment|/**    * This procedure does no work. By convention, procedure zero of any protocol    * takes no parameters and returns no results.    */
DECL|method|nullOp (int xidd, XDR in, XDR out)
specifier|public
name|XDR
name|nullOp
parameter_list|(
name|int
name|xidd
parameter_list|,
name|XDR
name|in
parameter_list|,
name|XDR
name|out
parameter_list|)
function_decl|;
comment|/**    * When a program first becomes available on a machine, it registers itself    * with the port mapper program on the same machine. The program passes its    * program number "prog", version number "vers", transport protocol number    * "prot", and the port "port" on which it awaits service request. The    * procedure returns a boolean reply whose value is "TRUE" if the procedure    * successfully established the mapping and "FALSE" otherwise. The procedure    * refuses to establish a mapping if one already exists for the tuple    * "(prog, vers, prot)".    */
DECL|method|set (int xid, XDR in, XDR out)
specifier|public
name|XDR
name|set
parameter_list|(
name|int
name|xid
parameter_list|,
name|XDR
name|in
parameter_list|,
name|XDR
name|out
parameter_list|)
function_decl|;
comment|/**    * When a program becomes unavailable, it should unregister itself with the    * port mapper program on the same machine. The parameters and results have    * meanings identical to those of "PMAPPROC_SET". The protocol and port number    * fields of the argument are ignored.    */
DECL|method|unset (int xid, XDR in, XDR out)
specifier|public
name|XDR
name|unset
parameter_list|(
name|int
name|xid
parameter_list|,
name|XDR
name|in
parameter_list|,
name|XDR
name|out
parameter_list|)
function_decl|;
comment|/**    * Given a program number "prog", version number "vers", and transport    * protocol number "prot", this procedure returns the port number on which the    * program is awaiting call requests. A port value of zeros means the program    * has not been registered. The "port" field of the argument is ignored.    */
DECL|method|getport (int xid, XDR in, XDR out)
specifier|public
name|XDR
name|getport
parameter_list|(
name|int
name|xid
parameter_list|,
name|XDR
name|in
parameter_list|,
name|XDR
name|out
parameter_list|)
function_decl|;
comment|/**    * This procedure enumerates all entries in the port mapper's database. The    * procedure takes no parameters and returns a list of program, version,    * protocol, and port values.    */
DECL|method|dump (int xid, XDR in, XDR out)
specifier|public
name|XDR
name|dump
parameter_list|(
name|int
name|xid
parameter_list|,
name|XDR
name|in
parameter_list|,
name|XDR
name|out
parameter_list|)
function_decl|;
block|}
end_interface

end_unit


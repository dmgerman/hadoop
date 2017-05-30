begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mount
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mount
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
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
comment|/**  * This is an interface that should be implemented for handle Mountd related  * requests. See RFC 1094 for more details.  */
end_comment

begin_interface
DECL|interface|MountInterface
specifier|public
interface|interface
name|MountInterface
block|{
comment|/** Mount procedures */
DECL|enum|MNTPROC
specifier|public
enum|enum
name|MNTPROC
block|{
comment|// the order of the values below are significant.
DECL|enumConstant|NULL
name|NULL
block|,
DECL|enumConstant|MNT
name|MNT
block|,
DECL|enumConstant|DUMP
name|DUMP
block|,
DECL|enumConstant|UMNT
name|UMNT
block|,
DECL|enumConstant|UMNTALL
name|UMNTALL
block|,
DECL|enumConstant|EXPORT
name|EXPORT
block|,
DECL|enumConstant|EXPORTALL
name|EXPORTALL
block|,
DECL|enumConstant|PATHCONF
name|PATHCONF
block|;
comment|/** @return the int value representing the procedure. */
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
comment|/** The procedure of given value.      * @param value specifies the procedure index      * @return the procedure corresponding to the value.      */
DECL|method|fromValue (int value)
specifier|public
specifier|static
name|MNTPROC
name|fromValue
parameter_list|(
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|<
literal|0
operator|||
name|value
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|values
argument_list|()
index|[
name|value
index|]
return|;
block|}
block|}
comment|/**    * MNTPRC_NULL - Do Nothing.    * @param out XDR response used in NFS protocol    * @param xid transaction id    * @param client represents IP address    * @return XDR response    */
DECL|method|nullOp (XDR out, int xid, InetAddress client)
specifier|public
name|XDR
name|nullOp
parameter_list|(
name|XDR
name|out
parameter_list|,
name|int
name|xid
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/**    * MNTPROC_MNT - Add mount entry.    * @param xdr XDR message used in NFS protocol    * @param out XDR response used in NFS protocol    * @param xid transaction id    * @param client represents IP address    * @return XDR response    */
DECL|method|mnt (XDR xdr, XDR out, int xid, InetAddress client)
specifier|public
name|XDR
name|mnt
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|XDR
name|out
parameter_list|,
name|int
name|xid
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/**    * MNTPROC_DUMP - Return mount entries.    * @param out XDR response used in NFS protocol    * @param xid transaction id    * @param client represents IP address    * @return XDR response    */
DECL|method|dump (XDR out, int xid, InetAddress client)
specifier|public
name|XDR
name|dump
parameter_list|(
name|XDR
name|out
parameter_list|,
name|int
name|xid
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/**    * MNTPROC_UMNT - Remove mount entry.    * @param xdr XDR message used in NFS protocol    * @param out XDR response used in NFS protocol    * @param xid transaction id    * @param client represents IP address    * @return XDR response    */
DECL|method|umnt (XDR xdr, XDR out, int xid, InetAddress client)
specifier|public
name|XDR
name|umnt
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|XDR
name|out
parameter_list|,
name|int
name|xid
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/**    * MNTPROC_UMNTALL - Remove all mount entries.    * @param out XDR response used in NFS protocol    * @param xid transaction id    * @param client represents IP address    * @return XDR response    */
DECL|method|umntall (XDR out, int xid, InetAddress client)
specifier|public
name|XDR
name|umntall
parameter_list|(
name|XDR
name|out
parameter_list|,
name|int
name|xid
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** MNTPROC_EXPORT and MNTPROC_EXPORTALL - Return export list */
comment|//public XDR exportall(XDR out, int xid, InetAddress client);
comment|/** MNTPROC_PATHCONF - POSIX pathconf information */
comment|//public XDR pathconf(XDR out, int xid, InetAddress client);
block|}
end_interface

end_unit


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
DECL|field|MNTPROC_NULL
specifier|public
specifier|static
name|int
name|MNTPROC_NULL
init|=
literal|0
decl_stmt|;
DECL|field|MNTPROC_MNT
specifier|public
specifier|static
name|int
name|MNTPROC_MNT
init|=
literal|1
decl_stmt|;
DECL|field|MNTPROC_DUMP
specifier|public
specifier|static
name|int
name|MNTPROC_DUMP
init|=
literal|2
decl_stmt|;
DECL|field|MNTPROC_UMNT
specifier|public
specifier|static
name|int
name|MNTPROC_UMNT
init|=
literal|3
decl_stmt|;
DECL|field|MNTPROC_UMNTALL
specifier|public
specifier|static
name|int
name|MNTPROC_UMNTALL
init|=
literal|4
decl_stmt|;
DECL|field|MNTPROC_EXPORT
specifier|public
specifier|static
name|int
name|MNTPROC_EXPORT
init|=
literal|5
decl_stmt|;
DECL|field|MNTPROC_EXPORTALL
specifier|public
specifier|static
name|int
name|MNTPROC_EXPORTALL
init|=
literal|6
decl_stmt|;
DECL|field|MNTPROC_PATHCONF
specifier|public
specifier|static
name|int
name|MNTPROC_PATHCONF
init|=
literal|7
decl_stmt|;
comment|/** MNTPROC_NULL - Do Nothing */
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
comment|/** MNTPROC_MNT - Add mount entry */
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
comment|/** MNTPROC_DUMP - Return mount entries */
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
comment|/** MNTPROC_UMNT - Remove mount entry */
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
comment|/** MNTPROC_UMNTALL - Remove all mount entries */
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


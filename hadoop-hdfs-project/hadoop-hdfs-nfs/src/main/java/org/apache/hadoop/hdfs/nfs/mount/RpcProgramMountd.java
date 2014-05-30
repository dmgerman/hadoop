begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.mount
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
operator|.
name|mount
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|DatagramSocket
import|;
end_import

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
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|hdfs
operator|.
name|DFSClient
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
name|hdfs
operator|.
name|nfs
operator|.
name|conf
operator|.
name|NfsConfigKeys
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
name|hdfs
operator|.
name|nfs
operator|.
name|conf
operator|.
name|NfsConfiguration
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsFileStatus
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|mount
operator|.
name|MountEntry
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
name|mount
operator|.
name|MountInterface
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
name|mount
operator|.
name|MountResponse
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
name|AccessPrivilege
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
name|NfsExports
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
name|FileHandle
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
name|RpcAcceptedReply
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
name|RpcCall
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
name|RpcInfo
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
name|RpcProgram
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
name|RpcResponse
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
name|RpcUtil
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
name|VerifierNone
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
name|security
operator|.
name|SecurityUtil
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * RPC program corresponding to mountd daemon. See {@link Mountd}.  */
end_comment

begin_class
DECL|class|RpcProgramMountd
specifier|public
class|class
name|RpcProgramMountd
extends|extends
name|RpcProgram
implements|implements
name|MountInterface
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RpcProgramMountd
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PROGRAM
specifier|public
specifier|static
specifier|final
name|int
name|PROGRAM
init|=
literal|100005
decl_stmt|;
DECL|field|VERSION_1
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_1
init|=
literal|1
decl_stmt|;
DECL|field|VERSION_2
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_2
init|=
literal|2
decl_stmt|;
DECL|field|VERSION_3
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_3
init|=
literal|3
decl_stmt|;
DECL|field|dfsClient
specifier|private
specifier|final
name|DFSClient
name|dfsClient
decl_stmt|;
comment|/** Synchronized list */
DECL|field|mounts
specifier|private
specifier|final
name|List
argument_list|<
name|MountEntry
argument_list|>
name|mounts
decl_stmt|;
comment|/** List that is unmodifiable */
DECL|field|exports
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|exports
decl_stmt|;
DECL|field|hostsMatcher
specifier|private
specifier|final
name|NfsExports
name|hostsMatcher
decl_stmt|;
DECL|method|RpcProgramMountd (NfsConfiguration config, DatagramSocket registrationSocket, boolean allowInsecurePorts)
specifier|public
name|RpcProgramMountd
parameter_list|(
name|NfsConfiguration
name|config
parameter_list|,
name|DatagramSocket
name|registrationSocket
parameter_list|,
name|boolean
name|allowInsecurePorts
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Note that RPC cache is not enabled
name|super
argument_list|(
literal|"mountd"
argument_list|,
literal|"localhost"
argument_list|,
name|config
operator|.
name|getInt
argument_list|(
name|NfsConfigKeys
operator|.
name|DFS_NFS_MOUNTD_PORT_KEY
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_MOUNTD_PORT_DEFAULT
argument_list|)
argument_list|,
name|PROGRAM
argument_list|,
name|VERSION_1
argument_list|,
name|VERSION_3
argument_list|,
name|registrationSocket
argument_list|,
name|allowInsecurePorts
argument_list|)
expr_stmt|;
name|exports
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|exports
operator|.
name|add
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|NfsConfigKeys
operator|.
name|DFS_NFS_EXPORT_POINT_KEY
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_EXPORT_POINT_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|hostsMatcher
operator|=
name|NfsExports
operator|.
name|getInstance
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|mounts
operator|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|MountEntry
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|SecurityUtil
operator|.
name|login
argument_list|(
name|config
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_KEYTAB_FILE_KEY
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_KERBEROS_PRINCIPAL_KEY
argument_list|)
expr_stmt|;
name|this
operator|.
name|dfsClient
operator|=
operator|new
name|DFSClient
argument_list|(
name|NameNode
operator|.
name|getAddress
argument_list|(
name|config
argument_list|)
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"MOUNT NULLOP : "
operator|+
literal|" client: "
operator|+
name|client
argument_list|)
expr_stmt|;
block|}
return|return
name|RpcAcceptedReply
operator|.
name|getAcceptInstance
argument_list|(
name|xid
argument_list|,
operator|new
name|VerifierNone
argument_list|()
argument_list|)
operator|.
name|write
argument_list|(
name|out
argument_list|)
return|;
block|}
annotation|@
name|Override
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
block|{
name|AccessPrivilege
name|accessPrivilege
init|=
name|hostsMatcher
operator|.
name|getAccessPrivilege
argument_list|(
name|client
argument_list|)
decl_stmt|;
if|if
condition|(
name|accessPrivilege
operator|==
name|AccessPrivilege
operator|.
name|NONE
condition|)
block|{
return|return
name|MountResponse
operator|.
name|writeMNTResponse
argument_list|(
name|Nfs3Status
operator|.
name|NFS3ERR_ACCES
argument_list|,
name|out
argument_list|,
name|xid
argument_list|,
literal|null
argument_list|)
return|;
block|}
name|String
name|path
init|=
name|xdr
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"MOUNT MNT path: "
operator|+
name|path
operator|+
literal|" client: "
operator|+
name|client
argument_list|)
expr_stmt|;
block|}
name|String
name|host
init|=
name|client
operator|.
name|getHostName
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got host: "
operator|+
name|host
operator|+
literal|" path: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|exports
operator|.
name|contains
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Path "
operator|+
name|path
operator|+
literal|" is not shared."
argument_list|)
expr_stmt|;
name|MountResponse
operator|.
name|writeMNTResponse
argument_list|(
name|Nfs3Status
operator|.
name|NFS3ERR_NOENT
argument_list|,
name|out
argument_list|,
name|xid
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
name|FileHandle
name|handle
init|=
literal|null
decl_stmt|;
try|try
block|{
name|HdfsFileStatus
name|exFileStatus
init|=
name|dfsClient
operator|.
name|getFileInfo
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|handle
operator|=
operator|new
name|FileHandle
argument_list|(
name|exFileStatus
operator|.
name|getFileId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't get handle for export:"
operator|+
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|MountResponse
operator|.
name|writeMNTResponse
argument_list|(
name|Nfs3Status
operator|.
name|NFS3ERR_NOENT
argument_list|,
name|out
argument_list|,
name|xid
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
assert|assert
operator|(
name|handle
operator|!=
literal|null
operator|)
assert|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Giving handle (fileId:"
operator|+
name|handle
operator|.
name|getFileId
argument_list|()
operator|+
literal|") to client for export "
operator|+
name|path
argument_list|)
expr_stmt|;
name|mounts
operator|.
name|add
argument_list|(
operator|new
name|MountEntry
argument_list|(
name|host
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|MountResponse
operator|.
name|writeMNTResponse
argument_list|(
name|Nfs3Status
operator|.
name|NFS3_OK
argument_list|,
name|out
argument_list|,
name|xid
argument_list|,
name|handle
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
annotation|@
name|Override
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
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"MOUNT NULLOP : "
operator|+
literal|" client: "
operator|+
name|client
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|MountEntry
argument_list|>
name|copy
init|=
operator|new
name|ArrayList
argument_list|<
name|MountEntry
argument_list|>
argument_list|(
name|mounts
argument_list|)
decl_stmt|;
name|MountResponse
operator|.
name|writeMountList
argument_list|(
name|out
argument_list|,
name|xid
argument_list|,
name|copy
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
annotation|@
name|Override
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
block|{
name|String
name|path
init|=
name|xdr
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"MOUNT UMNT path: "
operator|+
name|path
operator|+
literal|" client: "
operator|+
name|client
argument_list|)
expr_stmt|;
block|}
name|String
name|host
init|=
name|client
operator|.
name|getHostName
argument_list|()
decl_stmt|;
name|mounts
operator|.
name|remove
argument_list|(
operator|new
name|MountEntry
argument_list|(
name|host
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|RpcAcceptedReply
operator|.
name|getAcceptInstance
argument_list|(
name|xid
argument_list|,
operator|new
name|VerifierNone
argument_list|()
argument_list|)
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
annotation|@
name|Override
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
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"MOUNT UMNTALL : "
operator|+
literal|" client: "
operator|+
name|client
argument_list|)
expr_stmt|;
block|}
name|mounts
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|RpcAcceptedReply
operator|.
name|getAcceptInstance
argument_list|(
name|xid
argument_list|,
operator|new
name|VerifierNone
argument_list|()
argument_list|)
operator|.
name|write
argument_list|(
name|out
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|handleInternal (ChannelHandlerContext ctx, RpcInfo info)
specifier|public
name|void
name|handleInternal
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
block|{
name|RpcCall
name|rpcCall
init|=
operator|(
name|RpcCall
operator|)
name|info
operator|.
name|header
argument_list|()
decl_stmt|;
specifier|final
name|MNTPROC
name|mntproc
init|=
name|MNTPROC
operator|.
name|fromValue
argument_list|(
name|rpcCall
operator|.
name|getProcedure
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|xid
init|=
name|rpcCall
operator|.
name|getXid
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|info
operator|.
name|data
argument_list|()
operator|.
name|readableBytes
argument_list|()
index|]
decl_stmt|;
name|info
operator|.
name|data
argument_list|()
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|XDR
name|xdr
init|=
operator|new
name|XDR
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|XDR
name|out
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|InetAddress
name|client
init|=
operator|(
operator|(
name|InetSocketAddress
operator|)
name|info
operator|.
name|remoteAddress
argument_list|()
operator|)
operator|.
name|getAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|mntproc
operator|==
name|MNTPROC
operator|.
name|NULL
condition|)
block|{
name|out
operator|=
name|nullOp
argument_list|(
name|out
argument_list|,
name|xid
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mntproc
operator|==
name|MNTPROC
operator|.
name|MNT
condition|)
block|{
name|out
operator|=
name|mnt
argument_list|(
name|xdr
argument_list|,
name|out
argument_list|,
name|xid
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mntproc
operator|==
name|MNTPROC
operator|.
name|DUMP
condition|)
block|{
name|out
operator|=
name|dump
argument_list|(
name|out
argument_list|,
name|xid
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mntproc
operator|==
name|MNTPROC
operator|.
name|UMNT
condition|)
block|{
name|out
operator|=
name|umnt
argument_list|(
name|xdr
argument_list|,
name|out
argument_list|,
name|xid
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mntproc
operator|==
name|MNTPROC
operator|.
name|UMNTALL
condition|)
block|{
name|umntall
argument_list|(
name|out
argument_list|,
name|xid
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mntproc
operator|==
name|MNTPROC
operator|.
name|EXPORT
condition|)
block|{
comment|// Currently only support one NFS export
name|List
argument_list|<
name|NfsExports
argument_list|>
name|hostsMatchers
init|=
operator|new
name|ArrayList
argument_list|<
name|NfsExports
argument_list|>
argument_list|()
decl_stmt|;
name|hostsMatchers
operator|.
name|add
argument_list|(
name|hostsMatcher
argument_list|)
expr_stmt|;
name|out
operator|=
name|MountResponse
operator|.
name|writeExportList
argument_list|(
name|out
argument_list|,
name|xid
argument_list|,
name|exports
argument_list|,
name|hostsMatchers
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Invalid procedure
name|RpcAcceptedReply
operator|.
name|getInstance
argument_list|(
name|xid
argument_list|,
name|RpcAcceptedReply
operator|.
name|AcceptState
operator|.
name|PROC_UNAVAIL
argument_list|,
operator|new
name|VerifierNone
argument_list|()
argument_list|)
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|ChannelBuffer
name|buf
init|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|out
operator|.
name|asReadOnlyWrap
argument_list|()
operator|.
name|buffer
argument_list|()
argument_list|)
decl_stmt|;
name|RpcResponse
name|rsp
init|=
operator|new
name|RpcResponse
argument_list|(
name|buf
argument_list|,
name|info
operator|.
name|remoteAddress
argument_list|()
argument_list|)
decl_stmt|;
name|RpcUtil
operator|.
name|sendRpcResponse
argument_list|(
name|ctx
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isIdempotent (RpcCall call)
specifier|protected
name|boolean
name|isIdempotent
parameter_list|(
name|RpcCall
name|call
parameter_list|)
block|{
comment|// Not required, because cache is turned off
return|return
literal|false
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getExports ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getExports
parameter_list|()
block|{
return|return
name|this
operator|.
name|exports
return|;
block|}
block|}
end_class

end_unit


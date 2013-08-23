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
name|InetAddress
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
name|conf
operator|.
name|Configuration
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
name|security
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
name|hdfs
operator|.
name|nfs
operator|.
name|security
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
name|XDR
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
name|Channel
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
DECL|field|PORT
specifier|public
specifier|static
specifier|final
name|int
name|PORT
init|=
literal|4242
decl_stmt|;
comment|// Need DFSClient for branch-1 to get ExtendedHdfsFileStatus
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
DECL|method|RpcProgramMountd ()
specifier|public
name|RpcProgramMountd
parameter_list|()
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|RpcProgramMountd (List<String> exports)
specifier|public
name|RpcProgramMountd
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|exports
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|exports
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|RpcProgramMountd (List<String> exports, Configuration config)
specifier|public
name|RpcProgramMountd
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|exports
parameter_list|,
name|Configuration
name|config
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
name|PORT
argument_list|,
name|PROGRAM
argument_list|,
name|VERSION_1
argument_list|,
name|VERSION_3
argument_list|,
literal|0
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
name|this
operator|.
name|exports
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|exports
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
name|voidReply
argument_list|(
name|out
argument_list|,
name|xid
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
operator|+
literal|", exception:"
operator|+
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
name|voidReply
argument_list|(
name|out
argument_list|,
name|xid
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
name|voidReply
argument_list|(
name|out
argument_list|,
name|xid
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|handleInternal (RpcCall rpcCall, XDR xdr, XDR out, InetAddress client, Channel channel)
specifier|public
name|XDR
name|handleInternal
parameter_list|(
name|RpcCall
name|rpcCall
parameter_list|,
name|XDR
name|xdr
parameter_list|,
name|XDR
name|out
parameter_list|,
name|InetAddress
name|client
parameter_list|,
name|Channel
name|channel
parameter_list|)
block|{
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
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Invalid procedure
name|RpcAcceptedReply
operator|.
name|voidReply
argument_list|(
name|out
argument_list|,
name|xid
argument_list|,
name|RpcAcceptedReply
operator|.
name|AcceptState
operator|.
name|PROC_UNAVAIL
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
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
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.nfs3
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
name|nfs3
package|;
end_package

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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
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
name|daemon
operator|.
name|Daemon
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
name|daemon
operator|.
name|DaemonContext
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

begin_comment
comment|/**  * This class is used to allow the initial registration of the NFS gateway with  * the system portmap daemon to come from a privileged (&lt; 1024) port. This is  * necessary on certain operating systems to work around this bug in rpcbind:  *   * Red Hat: https://bugzilla.redhat.com/show_bug.cgi?id=731542  * SLES: https://bugzilla.novell.com/show_bug.cgi?id=823364  * Debian: https://bugs.debian.org/cgi-bin/bugreport.cgi?bug=594880  */
end_comment

begin_class
DECL|class|PrivilegedNfsGatewayStarter
specifier|public
class|class
name|PrivilegedNfsGatewayStarter
implements|implements
name|Daemon
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|PrivilegedNfsGatewayStarter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|args
specifier|private
name|String
index|[]
name|args
init|=
literal|null
decl_stmt|;
DECL|field|registrationSocket
specifier|private
name|DatagramSocket
name|registrationSocket
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init (DaemonContext context)
specifier|public
name|void
name|init
parameter_list|(
name|DaemonContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Initializing privileged NFS client socket..."
argument_list|)
expr_stmt|;
name|NfsConfiguration
name|conf
init|=
operator|new
name|NfsConfiguration
argument_list|()
decl_stmt|;
name|int
name|clientPort
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|NfsConfigKeys
operator|.
name|DFS_NFS_REGISTRATION_PORT_KEY
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_REGISTRATION_PORT_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|clientPort
argument_list|<
literal|1
operator|||
name|clientPort
argument_list|>
literal|1023
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Must start privileged NFS server with '"
operator|+
name|NfsConfigKeys
operator|.
name|DFS_NFS_REGISTRATION_PORT_KEY
operator|+
literal|"' configured to a "
operator|+
literal|"privileged port."
argument_list|)
throw|;
block|}
try|try
block|{
name|InetSocketAddress
name|socketAddress
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|clientPort
argument_list|)
decl_stmt|;
name|registrationSocket
operator|=
operator|new
name|DatagramSocket
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|registrationSocket
operator|.
name|setReuseAddress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|registrationSocket
operator|.
name|bind
argument_list|(
name|socketAddress
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Init failed for port="
operator|+
name|clientPort
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|args
operator|=
name|context
operator|.
name|getArguments
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|Nfs3
operator|.
name|startService
argument_list|(
name|args
argument_list|,
name|registrationSocket
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Nothing to do.
block|}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
if|if
condition|(
name|registrationSocket
operator|!=
literal|null
operator|&&
operator|!
name|registrationSocket
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|registrationSocket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


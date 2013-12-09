begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|qjournal
operator|.
name|server
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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
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
name|DFSConfigKeys
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
name|DFSUtil
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
name|common
operator|.
name|JspHelper
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
name|http
operator|.
name|HttpServer
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
name|net
operator|.
name|NetUtils
import|;
end_import

begin_comment
comment|/**  * Encapsulates the HTTP server started by the Journal Service.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|JournalNodeHttpServer
specifier|public
class|class
name|JournalNodeHttpServer
block|{
DECL|field|JN_ATTRIBUTE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|JN_ATTRIBUTE_KEY
init|=
literal|"localjournal"
decl_stmt|;
DECL|field|httpServer
specifier|private
name|HttpServer
name|httpServer
decl_stmt|;
DECL|field|localJournalNode
specifier|private
name|JournalNode
name|localJournalNode
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|method|JournalNodeHttpServer (Configuration conf, JournalNode jn)
name|JournalNodeHttpServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|JournalNode
name|jn
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|localJournalNode
operator|=
name|jn
expr_stmt|;
block|}
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|InetSocketAddress
name|httpAddr
init|=
name|getAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|String
name|httpsAddrString
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_HTTPS_ADDRESS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_HTTPS_ADDRESS_DEFAULT
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|httpsAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|httpsAddrString
argument_list|)
decl_stmt|;
name|HttpServer
operator|.
name|Builder
name|builder
init|=
name|DFSUtil
operator|.
name|httpServerTemplateForNNAndJN
argument_list|(
name|conf
argument_list|,
name|httpAddr
argument_list|,
name|httpsAddr
argument_list|,
literal|"journal"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_INTERNAL_SPNEGO_USER_NAME_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_KEYTAB_FILE_KEY
argument_list|)
decl_stmt|;
name|httpServer
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|httpServer
operator|.
name|setAttribute
argument_list|(
name|JN_ATTRIBUTE_KEY
argument_list|,
name|localJournalNode
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|setAttribute
argument_list|(
name|JspHelper
operator|.
name|CURRENT_CONF
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|addInternalServlet
argument_list|(
literal|"getJournal"
argument_list|,
literal|"/getJournal"
argument_list|,
name|GetJournalEditServlet
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|httpServer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|httpServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Return the actual address bound to by the running server.    */
annotation|@
name|Deprecated
DECL|method|getAddress ()
specifier|public
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
name|InetSocketAddress
name|addr
init|=
name|httpServer
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
assert|assert
name|addr
operator|.
name|getPort
argument_list|()
operator|!=
literal|0
assert|;
return|return
name|addr
return|;
block|}
comment|/**    * Return the URI that locates the HTTP server.    */
DECL|method|getServerURI ()
name|URI
name|getServerURI
parameter_list|()
block|{
comment|// getHttpClientScheme() only returns https for HTTPS_ONLY policy. This
comment|// matches the behavior that the first connector is a HTTPS connector only
comment|// for HTTPS_ONLY policy.
name|InetSocketAddress
name|addr
init|=
name|httpServer
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|URI
operator|.
name|create
argument_list|(
name|DFSUtil
operator|.
name|getHttpClientScheme
argument_list|(
name|conf
argument_list|)
operator|+
literal|"://"
operator|+
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|addr
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getAddress (Configuration conf)
specifier|private
specifier|static
name|InetSocketAddress
name|getAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|addr
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_HTTP_ADDRESS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_HTTP_ADDRESS_DEFAULT
argument_list|)
decl_stmt|;
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|addr
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_HTTP_PORT_DEFAULT
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_HTTP_ADDRESS_KEY
argument_list|)
return|;
block|}
DECL|method|getJournalFromContext (ServletContext context, String jid)
specifier|public
specifier|static
name|Journal
name|getJournalFromContext
parameter_list|(
name|ServletContext
name|context
parameter_list|,
name|String
name|jid
parameter_list|)
throws|throws
name|IOException
block|{
name|JournalNode
name|jn
init|=
operator|(
name|JournalNode
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
name|JN_ATTRIBUTE_KEY
argument_list|)
decl_stmt|;
return|return
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|jid
argument_list|)
return|;
block|}
DECL|method|getConfFromContext (ServletContext context)
specifier|public
specifier|static
name|Configuration
name|getConfFromContext
parameter_list|(
name|ServletContext
name|context
parameter_list|)
block|{
return|return
operator|(
name|Configuration
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
name|JspHelper
operator|.
name|CURRENT_CONF
argument_list|)
return|;
block|}
block|}
end_class

end_unit


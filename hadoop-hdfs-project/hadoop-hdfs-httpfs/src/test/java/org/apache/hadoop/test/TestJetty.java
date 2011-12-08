begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
import|;
end_import

begin_comment
comment|/**  * Annotation for {@link TestJettyHelper} subclasses to indicate that the test method  * requires a Jetty servlet-container.  *<p/>  * The {@link TestJettyHelper#getJettyServer()} returns a ready to configure Jetty  * servlet-container. After registering contexts, servlets, filters the the Jetty  * server must be started (<code>getJettyServer.start()</code>. The Jetty server  * is automatically stopped at the end of the test method invocation.  *<p/>  * Use the {@link TestJettyHelper#getJettyURL()} to obtain the base URL  * (schema://host:port) of the Jetty server.  *<p/>  * Refer to the {@link HTestCase} class for more details.  */
end_comment

begin_annotation_defn
annotation|@
name|Retention
argument_list|(
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|METHOD
argument_list|)
DECL|annotation|TestJetty
specifier|public
annotation_defn|@interface
name|TestJetty
block|{ }
end_annotation_defn

end_unit


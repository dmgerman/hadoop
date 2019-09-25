begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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

begin_comment
comment|/**  * The Path counterpoint to {@link StreamCapabilities}; a query to see if,  * a FileSystem/FileContext instance has a specific capability under the given  * path.  * Other classes may also implement the interface, as desired.  *  * See {@link CommonPathCapabilities} for the well-known capabilities.  */
end_comment

begin_interface
DECL|interface|PathCapabilities
specifier|public
interface|interface
name|PathCapabilities
block|{
comment|/**    * Probe for a specific capability under the given path.    * If the function returns {@code true}, this instance is explicitly    * declaring that the capability is available.    * If the function returns {@code false}, it can mean one of:    *<ul>    *<li>The capability is not known.</li>    *<li>The capability is known but it is not supported.</li>    *<li>The capability is known but the filesystem does not know if it    *   is supported under the supplied path.</li>    *</ul>    * The core guarantee which a caller can rely on is: if the predicate    * returns true, then the specific operation/behavior can be expected to be    * supported. However a specific call may be rejected for permission reasons,    * the actual file/directory not being present, or some other failure during    * the attempted execution of the operation.    *<p>    * Implementors: {@link org.apache.hadoop.fs.impl.PathCapabilitiesSupport}    * can be used to help implement this method.    * @param path path to query the capability of.    * @param capability non-null, non-empty string to query the path for support.    * @return true if the capability is supported under that part of the FS.    * @throws IOException this should not be raised, except on problems    * resolving paths or relaying the call.    * @throws IllegalArgumentException invalid arguments    */
DECL|method|hasPathCapability (Path path, String capability)
name|boolean
name|hasPathCapability
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|capability
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit


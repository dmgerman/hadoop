begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.retry
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * An implementer of this interface is capable of providing proxy objects for  * use in IPC communication, and potentially modifying these objects or creating  * entirely new ones in the event of certain types of failures. The  * determination of whether or not to fail over is handled by  * {@link RetryPolicy}.  */
end_comment

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|FailoverProxyProvider
specifier|public
interface|interface
name|FailoverProxyProvider
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Closeable
block|{
DECL|class|ProxyInfo
specifier|static
class|class
name|ProxyInfo
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|proxy
specifier|public
name|T
name|proxy
decl_stmt|;
comment|/*      * The information (e.g., the IP address) of the current proxy object. It      * provides information for debugging purposes.      */
DECL|field|proxyInfo
specifier|public
name|String
name|proxyInfo
decl_stmt|;
DECL|method|ProxyInfo (T proxy, String proxyInfo)
specifier|public
name|ProxyInfo
parameter_list|(
name|T
name|proxy
parameter_list|,
name|String
name|proxyInfo
parameter_list|)
block|{
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
name|this
operator|.
name|proxyInfo
operator|=
name|proxyInfo
expr_stmt|;
block|}
DECL|method|proxyName ()
specifier|private
name|String
name|proxyName
parameter_list|()
block|{
return|return
name|proxy
operator|!=
literal|null
condition|?
name|proxy
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
else|:
literal|"UnknownProxy"
return|;
block|}
DECL|method|getString (String methodName)
specifier|public
name|String
name|getString
parameter_list|(
name|String
name|methodName
parameter_list|)
block|{
return|return
name|proxyName
argument_list|()
operator|+
literal|"."
operator|+
name|methodName
operator|+
literal|" over "
operator|+
name|proxyInfo
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|proxyName
argument_list|()
operator|+
literal|" over "
operator|+
name|proxyInfo
return|;
block|}
block|}
comment|/**    * Get the proxy object which should be used until the next failover event    * occurs.    *     * @return the proxy object to invoke methods upon    */
DECL|method|getProxy ()
specifier|public
name|ProxyInfo
argument_list|<
name|T
argument_list|>
name|getProxy
parameter_list|()
function_decl|;
comment|/**    * Called whenever the associated {@link RetryPolicy} determines that an error    * warrants failing over.    *     * @param currentProxy    *          the proxy object which was being used before this failover event    */
DECL|method|performFailover (T currentProxy)
specifier|public
name|void
name|performFailover
parameter_list|(
name|T
name|currentProxy
parameter_list|)
function_decl|;
comment|/**    * Return a reference to the interface this provider's proxy objects actually    * implement. If any of the methods on this interface are annotated as being    * {@link Idempotent} or {@link AtMostOnce}, then this fact will be passed to    * the {@link RetryPolicy#shouldRetry(Exception, int, int, boolean)} method on    * error, for use in determining whether or not failover should be attempted.    *     * @return the interface implemented by the proxy objects returned by    *         {@link FailoverProxyProvider#getProxy()}    */
DECL|method|getInterface ()
specifier|public
name|Class
argument_list|<
name|T
argument_list|>
name|getInterface
parameter_list|()
function_decl|;
block|}
end_interface

end_unit


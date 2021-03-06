begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
operator|.
name|Private
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
operator|.
name|Public
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
operator|.
name|Evolving
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationMasterProtocol
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
name|yarn
operator|.
name|api
operator|.
name|ContainerManagementProtocol
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Token
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
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|async
operator|.
name|AMRMClientAsync
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
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|async
operator|.
name|NMClientAsync
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
comment|/**  * NMTokenCache manages NMTokens required for an Application Master  * communicating with individual NodeManagers.  *<p>  * By default YARN client libraries {@link AMRMClient} and {@link NMClient} use  * {@link #getSingleton()} instance of the cache.  *<ul>  *<li>  *     Using the singleton instance of the cache is appropriate when running a  *     single ApplicationMaster in the same JVM.  *</li>  *<li>  *     When using the singleton, users don't need to do anything special,  *     {@link AMRMClient} and {@link NMClient} are already set up to use the  *     default singleton {@link NMTokenCache}  *</li>  *</ul>  * If running multiple Application Masters in the same JVM, a different cache  * instance should be used for each Application Master.  *<ul>  *<li>  *     If using the {@link AMRMClient} and the {@link NMClient}, setting up  *     and using an instance cache is as follows:  *<pre>  *   NMTokenCache nmTokenCache = new NMTokenCache();  *   AMRMClient rmClient = AMRMClient.createAMRMClient();  *   NMClient nmClient = NMClient.createNMClient();  *   nmClient.setNMTokenCache(nmTokenCache);  *   ...  *</pre>  *</li>  *<li>  *     If using the {@link AMRMClientAsync} and the {@link NMClientAsync},  *     setting up and using an instance cache is as follows:  *<pre>  *   NMTokenCache nmTokenCache = new NMTokenCache();  *   AMRMClient rmClient = AMRMClient.createAMRMClient();  *   NMClient nmClient = NMClient.createNMClient();  *   nmClient.setNMTokenCache(nmTokenCache);  *   AMRMClientAsync rmClientAsync = new AMRMClientAsync(rmClient, 1000, [AMRM_CALLBACK]);  *   NMClientAsync nmClientAsync = new NMClientAsync("nmClient", nmClient, [NM_CALLBACK]);  *   ...  *</pre>  *</li>  *<li>  *     If using {@link ApplicationMasterProtocol} and  *     {@link ContainerManagementProtocol} directly, setting up and using an  *     instance cache is as follows:  *<pre>  *   NMTokenCache nmTokenCache = new NMTokenCache();  *   ...  *   ApplicationMasterProtocol amPro = ClientRMProxy.createRMProxy(conf, ApplicationMasterProtocol.class);  *   ...  *   AllocateRequest allocateRequest = ...  *   ...  *   AllocateResponse allocateResponse = rmClient.allocate(allocateRequest);  *   for (NMToken token : allocateResponse.getNMTokens()) {  *     nmTokenCache.setToken(token.getNodeId().toString(), token.getToken());  *   }  *   ...  *   ContainerManagementProtocolProxy nmPro = ContainerManagementProtocolProxy(conf, nmTokenCache);  *   ...  *   nmPro.startContainer(container, containerContext);  *   ...  *</pre>  *</li>  *</ul>  * It is also possible to mix the usage of a client ({@code AMRMClient} or  * {@code NMClient}, or the async versions of them) with a protocol proxy  * ({@code ContainerManagementProtocolProxy} or  * {@code ApplicationMasterProtocol}).  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|NMTokenCache
specifier|public
class|class
name|NMTokenCache
block|{
DECL|field|NM_TOKEN_CACHE
specifier|private
specifier|static
specifier|final
name|NMTokenCache
name|NM_TOKEN_CACHE
init|=
operator|new
name|NMTokenCache
argument_list|()
decl_stmt|;
comment|/**    * Returns the singleton NM token cache.    *    * @return the singleton NM token cache.    */
DECL|method|getSingleton ()
specifier|public
specifier|static
name|NMTokenCache
name|getSingleton
parameter_list|()
block|{
return|return
name|NM_TOKEN_CACHE
return|;
block|}
comment|/**    * Returns NMToken, null if absent. Only the singleton obtained from    * {@link #getSingleton()} is looked at for the tokens. If you are using your    * own NMTokenCache that is different from the singleton, use    * {@link #getToken(String) }    *     * @param nodeAddr    * @return {@link Token} NMToken required for communicating with node manager    */
annotation|@
name|Public
DECL|method|getNMToken (String nodeAddr)
specifier|public
specifier|static
name|Token
name|getNMToken
parameter_list|(
name|String
name|nodeAddr
parameter_list|)
block|{
return|return
name|NM_TOKEN_CACHE
operator|.
name|getToken
argument_list|(
name|nodeAddr
argument_list|)
return|;
block|}
comment|/**    * Sets the NMToken for node address only in the singleton obtained from    * {@link #getSingleton()}. If you are using your own NMTokenCache that is    * different from the singleton, use {@link #setToken(String, Token) }    *     * @param nodeAddr    *          node address (host:port)    * @param token    *          NMToken    */
annotation|@
name|Public
DECL|method|setNMToken (String nodeAddr, Token token)
specifier|public
specifier|static
name|void
name|setNMToken
parameter_list|(
name|String
name|nodeAddr
parameter_list|,
name|Token
name|token
parameter_list|)
block|{
name|NM_TOKEN_CACHE
operator|.
name|setToken
argument_list|(
name|nodeAddr
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
DECL|field|nmTokens
specifier|private
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Token
argument_list|>
name|nmTokens
decl_stmt|;
comment|/**    * Creates a NM token cache instance.    */
DECL|method|NMTokenCache ()
specifier|public
name|NMTokenCache
parameter_list|()
block|{
name|nmTokens
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Token
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns NMToken, null if absent    * @param nodeAddr    * @return {@link Token} NMToken required for communicating with node    *         manager    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getToken (String nodeAddr)
specifier|public
name|Token
name|getToken
parameter_list|(
name|String
name|nodeAddr
parameter_list|)
block|{
return|return
name|nmTokens
operator|.
name|get
argument_list|(
name|nodeAddr
argument_list|)
return|;
block|}
comment|/**    * Sets the NMToken for node address    * @param nodeAddr node address (host:port)    * @param token NMToken    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|setToken (String nodeAddr, Token token)
specifier|public
name|void
name|setToken
parameter_list|(
name|String
name|nodeAddr
parameter_list|,
name|Token
name|token
parameter_list|)
block|{
name|nmTokens
operator|.
name|put
argument_list|(
name|nodeAddr
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns true if NMToken is present in cache.    */
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|containsToken (String nodeAddr)
specifier|public
name|boolean
name|containsToken
parameter_list|(
name|String
name|nodeAddr
parameter_list|)
block|{
return|return
name|nmTokens
operator|.
name|containsKey
argument_list|(
name|nodeAddr
argument_list|)
return|;
block|}
comment|/**    * Returns the number of NMTokens present in cache.    */
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|numberOfTokensInCache ()
specifier|public
name|int
name|numberOfTokensInCache
parameter_list|()
block|{
return|return
name|nmTokens
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Removes NMToken for specified node manager    * @param nodeAddr node address (host:port)    */
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|removeToken (String nodeAddr)
specifier|public
name|void
name|removeToken
parameter_list|(
name|String
name|nodeAddr
parameter_list|)
block|{
name|nmTokens
operator|.
name|remove
argument_list|(
name|nodeAddr
argument_list|)
expr_stmt|;
block|}
comment|/**    * It will remove all the nm tokens from its cache    */
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|clearCache ()
specifier|public
name|void
name|clearCache
parameter_list|()
block|{
name|nmTokens
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit


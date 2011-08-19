begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.applicationsmanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|applicationsmanager
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|net
operator|.
name|NetworkTopology
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
name|Node
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
name|NodeBase
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
name|protocolrecords
operator|.
name|SubmitApplicationRequest
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
name|ApplicationId
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
name|ApplicationState
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
name|ApplicationSubmissionContext
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
name|Container
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
name|NodeId
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
name|Priority
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
name|Resource
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
name|ResourceRequest
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
name|event
operator|.
name|EventHandler
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|security
operator|.
name|ApplicationTokenSecretManager
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
name|server
operator|.
name|resourcemanager
operator|.
name|ClientRMService
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContextImpl
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
name|server
operator|.
name|resourcemanager
operator|.
name|amlauncher
operator|.
name|AMLauncherEventType
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
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|MemStore
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNodeImpl
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|NodeResponse
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|ResourceScheduler
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|YarnScheduler
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fifo
operator|.
name|FifoScheduler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Testing application cleanup (notifications to nodemanagers).  *  */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|TestApplicationCleanup
specifier|public
class|class
name|TestApplicationCleanup
block|{
comment|//  private static final Log LOG = LogFactory.getLog(TestApplicationCleanup.class);
comment|//  private static RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
comment|//  private AtomicInteger waitForState = new AtomicInteger(0);
comment|//  private ResourceScheduler scheduler;
comment|//  private final int memoryCapability = 1024;
comment|//  private ExtASM asm;
comment|//  private static final int memoryNeeded = 100;
comment|//
comment|//  private final RMContext context = new RMContextImpl(new MemStore());
comment|//  private ClientRMService clientService;
comment|//
comment|//  @Before
comment|//  public void setUp() {
comment|//    new DummyApplicationTracker();
comment|//    scheduler = new FifoScheduler();
comment|//    context.getDispatcher().register(ApplicationTrackerEventType.class, scheduler);
comment|//    Configuration conf = new Configuration();
comment|//    context.getDispatcher().init(conf);
comment|//    context.getDispatcher().start();
comment|//    asm = new ExtASM(new ApplicationTokenSecretManager(), scheduler);
comment|//    asm.init(conf);
comment|//    clientService = new ClientRMService(context,
comment|//        asm.getAmLivelinessMonitor(), asm.getClientToAMSecretManager(),
comment|//        scheduler);
comment|//  }
comment|//
comment|//  @After
comment|//  public void tearDown() {
comment|//
comment|//  }
comment|//
comment|//
comment|//  private class DummyApplicationTracker implements EventHandler<ASMEvent
comment|//<ApplicationTrackerEventType>> {
comment|//
comment|//    public DummyApplicationTracker() {
comment|//      context.getDispatcher().register(ApplicationTrackerEventType.class, this);
comment|//    }
comment|//    @Override
comment|//    public void handle(ASMEvent<ApplicationTrackerEventType> event) {
comment|//    }
comment|//
comment|//  }
comment|//  private class ExtASM extends ApplicationsManagerImpl {
comment|//    boolean schedulerCleanupCalled = false;
comment|//    boolean launcherLaunchCalled = false;
comment|//    boolean launcherCleanupCalled = false;
comment|//    boolean schedulerScheduleCalled = false;
comment|//
comment|//    private class DummyApplicationMasterLauncher implements EventHandler<ASMEvent<AMLauncherEventType>> {
comment|//      private AtomicInteger notify = new AtomicInteger(0);
comment|//      private AppAttempt application;
comment|//
comment|//      public DummyApplicationMasterLauncher(RMContext context) {
comment|//        context.getDispatcher().register(AMLauncherEventType.class, this);
comment|//      }
comment|//
comment|//      @Override
comment|//      public void handle(ASMEvent<AMLauncherEventType> appEvent) {
comment|//        AMLauncherEventType event = appEvent.getType();
comment|//        switch (event) {
comment|//        case CLEANUP:
comment|//          launcherCleanupCalled = true;
comment|//          break;
comment|//        case LAUNCH:
comment|//          LOG.info("Launcher Launch called");
comment|//          launcherLaunchCalled = true;
comment|//          application = appEvent.getApplication();
comment|//          context.getDispatcher().getEventHandler().handle(
comment|//              new ApplicationEvent(ApplicationEventType.LAUNCHED,
comment|//                  application.getApplicationID()));
comment|//          break;
comment|//        default:
comment|//          break;
comment|//        }
comment|//      }
comment|//    }
comment|//
comment|//    private class DummySchedulerNegotiator implements EventHandler<ASMEvent<SNEventType>> {
comment|//      private AtomicInteger snnotify = new AtomicInteger(0);
comment|//      AppAttempt application;
comment|//      public  DummySchedulerNegotiator(RMContext context) {
comment|//        context.getDispatcher().register(SNEventType.class, this);
comment|//      }
comment|//
comment|//      @Override
comment|//      public void handle(ASMEvent<SNEventType> appEvent) {
comment|//        SNEventType event = appEvent.getType();
comment|//        switch (event) {
comment|//        case RELEASE:
comment|//          schedulerCleanupCalled = true;
comment|//          break;
comment|//        case SCHEDULE:
comment|//          schedulerScheduleCalled = true;
comment|//          application = appEvent.getAppAttempt();
comment|//          context.getDispatcher().getEventHandler().handle(
comment|//              new AMAllocatedEvent(application.getApplicationID(),
comment|//                  application.getMasterContainer()));
comment|//        default:
comment|//          break;
comment|//        }
comment|//      }
comment|//
comment|//    }
comment|//    public ExtASM(ApplicationTokenSecretManager applicationTokenSecretManager,
comment|//        YarnScheduler scheduler) {
comment|//      super(applicationTokenSecretManager, scheduler, context);
comment|//    }
comment|//
comment|//    @Override
comment|//    protected EventHandler<ASMEvent<SNEventType>> createNewSchedulerNegotiator(
comment|//        YarnScheduler scheduler) {
comment|//      return new DummySchedulerNegotiator(context);
comment|//    }
comment|//
comment|//    @Override
comment|//    protected EventHandler<ASMEvent<AMLauncherEventType>> createNewApplicationMasterLauncher(
comment|//        ApplicationTokenSecretManager tokenSecretManager) {
comment|//      return new DummyApplicationMasterLauncher(context);
comment|//    }
comment|//
comment|//  }
comment|//
comment|//  private void waitForState(ApplicationState
comment|//      finalState, AppAttempt application) throws Exception {
comment|//    int count = 0;
comment|//    while(application.getState() != finalState&& count< 10) {
comment|//      Thread.sleep(500);
comment|//      count++;
comment|//    }
comment|//    Assert.assertEquals(finalState, application.getState());
comment|//  }
comment|//
comment|//
comment|//  private ResourceRequest createNewResourceRequest(int capability, int i) {
comment|//    ResourceRequest request = recordFactory.newRecordInstance(ResourceRequest.class);
comment|//    request.setCapability(recordFactory.newRecordInstance(Resource.class));
comment|//    request.getCapability().setMemory(capability);
comment|//    request.setNumContainers(1);
comment|//    request.setPriority(recordFactory.newRecordInstance(Priority.class));
comment|//    request.getPriority().setPriority(i);
comment|//    request.setHostName("*");
comment|//    return request;
comment|//  }
comment|//
comment|//  protected RMNode addNodes(String commonName, int i, int memoryCapability) throws IOException {
comment|//    NodeId nodeId = recordFactory.newRecordInstance(NodeId.class);
comment|//    nodeId.setId(i);
comment|//    String hostName = commonName + "_" + i;
comment|//    Node node = new NodeBase(hostName, NetworkTopology.DEFAULT_RACK);
comment|//    Resource capability = recordFactory.newRecordInstance(Resource.class);
comment|//    capability.setMemory(memoryCapability);
comment|//    return new RMNodeImpl(nodeId, hostName, i, -i, node, capability);
comment|//  }
comment|//
comment|//  @Test
comment|//  public void testApplicationCleanUp() throws Exception {
comment|//    ApplicationId appID = clientService.getNewApplicationId();
comment|//    ApplicationSubmissionContext submissionContext = recordFactory.newRecordInstance(ApplicationSubmissionContext.class);
comment|//    submissionContext.setApplicationId(appID);
comment|//    submissionContext.setQueue("queuename");
comment|//    submissionContext.setUser("dummyuser");
comment|//    SubmitApplicationRequest request = recordFactory
comment|//        .newRecordInstance(SubmitApplicationRequest.class);
comment|//    request.setApplicationSubmissionContext(submissionContext);
comment|//    clientService.submitApplication(request);
comment|//    waitForState(ApplicationState.LAUNCHED, context.getApplications().get(
comment|//        appID));
comment|//    List<ResourceRequest> reqs = new ArrayList<ResourceRequest>();
comment|//    ResourceRequest req = createNewResourceRequest(100, 1);
comment|//    reqs.add(req);
comment|//    reqs.add(createNewResourceRequest(memoryNeeded, 2));
comment|//    List<Container> release = new ArrayList<Container>();
comment|//    scheduler.allocate(appID, reqs, release);
comment|//    ArrayList<RMNode> nodesAdded = new ArrayList<RMNode>();
comment|//    for (int i = 0; i< 10; i++) {
comment|//      nodesAdded.add(addNodes("localhost", i, memoryCapability));
comment|//    }
comment|//    /* let one node heartbeat */
comment|//    Map<String, List<Container>> containers = new HashMap<String, List<Container>>();
comment|//    RMNode firstNode = nodesAdded.get(0);
comment|//    int firstNodeMemory = firstNode.getAvailableResource().getMemory();
comment|//    RMNode secondNode = nodesAdded.get(1);
comment|//
comment|//    context.getNodesCollection().updateListener(firstNode, containers);
comment|//    context.getNodesCollection().updateListener(secondNode, containers);
comment|//    LOG.info("Available resource on first node" + firstNode.getAvailableResource());
comment|//    LOG.info("Available resource on second node" + secondNode.getAvailableResource());
comment|//    /* only allocate the containers to the first node */
comment|//    Assert.assertEquals((firstNodeMemory - (2 * memoryNeeded)), firstNode
comment|//        .getAvailableResource().getMemory());
comment|//    context.getDispatcher().getEventHandler().handle(
comment|//        new ApplicationEvent(ApplicationEventType.KILL, appID));
comment|//    while (asm.launcherCleanupCalled != true) {
comment|//      Thread.sleep(500);
comment|//    }
comment|//    Assert.assertTrue(asm.launcherCleanupCalled);
comment|//    Assert.assertTrue(asm.launcherLaunchCalled);
comment|//    Assert.assertTrue(asm.schedulerCleanupCalled);
comment|//    Assert.assertTrue(asm.schedulerScheduleCalled);
comment|//    /* check for update of completed application */
comment|//    context.getNodesCollection().updateListener(firstNode, containers);
comment|//    NodeResponse response = firstNode.statusUpdate(containers);
comment|//    Assert.assertTrue(response.getFinishedApplications().contains(appID));
comment|//    LOG.info("The containers to clean up " + response.getContainersToCleanUp().size());
comment|//    Assert.assertEquals(2, response.getContainersToCleanUp().size());
comment|//  }
block|}
end_class

end_unit


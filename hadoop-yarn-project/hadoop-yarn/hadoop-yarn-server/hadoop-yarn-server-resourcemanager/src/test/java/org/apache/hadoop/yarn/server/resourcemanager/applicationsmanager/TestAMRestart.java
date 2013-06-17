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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|YarnApplicationState
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
name|ContainerId
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
name|QueueInfo
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
name|QueueUserACLInfo
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
name|conf
operator|.
name|YarnConfiguration
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
name|ResourceManager
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
name|RMStateStore
operator|.
name|RMState
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
name|resource
operator|.
name|Resources
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
name|scheduler
operator|.
name|Allocation
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
name|security
operator|.
name|AMRMTokenSecretManager
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
name|security
operator|.
name|BaseContainerTokenSecretManager
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Test to restart the AM on failure.  *  */
end_comment

begin_class
DECL|class|TestAMRestart
specifier|public
class|class
name|TestAMRestart
block|{
comment|//  private static final Log LOG = LogFactory.getLog(TestAMRestart.class);
comment|//  ApplicationsManagerImpl appImpl;
comment|//  RMContext asmContext = new RMContextImpl(new MemStore());
comment|//  ApplicationTokenSecretManager appTokenSecretManager =
comment|//    new ApplicationTokenSecretManager();
comment|//  DummyResourceScheduler scheduler;
comment|//  private ClientRMService clientRMService;
comment|//  int count = 0;
comment|//  ApplicationId appID;
comment|//  final int maxFailures = 3;
comment|//  AtomicInteger launchNotify = new AtomicInteger();
comment|//  AtomicInteger schedulerNotify = new AtomicInteger();
comment|//  volatile boolean stop = false;
comment|//  int schedulerAddApplication = 0;
comment|//  int schedulerRemoveApplication = 0;
comment|//  int launcherLaunchCalled = 0;
comment|//  int launcherCleanupCalled = 0;
comment|//  private final static RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
comment|//
comment|//  private class ExtApplicationsManagerImpl extends ApplicationsManagerImpl {
comment|//    public ExtApplicationsManagerImpl(
comment|//        ApplicationTokenSecretManager applicationTokenSecretManager,
comment|//        YarnScheduler scheduler, RMContext asmContext) {
comment|//      super(applicationTokenSecretManager, scheduler, asmContext);
comment|//    }
comment|//
comment|//    @Override
comment|//    public EventHandler<ASMEvent<AMLauncherEventType>> createNewApplicationMasterLauncher(
comment|//        ApplicationTokenSecretManager tokenSecretManager) {
comment|//      return new DummyAMLauncher();
comment|//    }
comment|//  }
comment|//
comment|//  private class DummyAMLauncher implements EventHandler<ASMEvent<AMLauncherEventType>> {
comment|//
comment|//    public DummyAMLauncher() {
comment|//      asmContext.getDispatcher().register(AMLauncherEventType.class, this);
comment|//      new Thread() {
comment|//        public void run() {
comment|//          while (!stop) {
comment|//            LOG.info("DEBUG -- waiting for launch");
comment|//            synchronized(launchNotify) {
comment|//              while (launchNotify.get() == 0) {
comment|//                try {
comment|//                  launchNotify.wait();
comment|//                } catch (InterruptedException e) {
comment|//                }
comment|//              }
comment|//              asmContext.getDispatcher().getEventHandler().handle(
comment|//                  new ApplicationEvent(
comment|//                      ApplicationEventType.LAUNCHED, appID));
comment|//              launchNotify.addAndGet(-1);
comment|//            }
comment|//          }
comment|//        }
comment|//      }.start();
comment|//    }
comment|//
comment|//    @Override
comment|//    public void handle(ASMEvent<AMLauncherEventType> event) {
comment|//      switch (event.getType()) {
comment|//      case CLEANUP:
comment|//        launcherCleanupCalled++;
comment|//        break;
comment|//      case LAUNCH:
comment|//        LOG.info("DEBUG -- launching");
comment|//        launcherLaunchCalled++;
comment|//        synchronized (launchNotify) {
comment|//          launchNotify.addAndGet(1);
comment|//          launchNotify.notify();
comment|//        }
comment|//        break;
comment|//      default:
comment|//        break;
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  private class DummyResourceScheduler implements ResourceScheduler {
comment|//
comment|//    @Override
comment|//    public void removeNode(RMNode node) {
comment|//    }
comment|//
comment|//    @Override
comment|//    public Allocation allocate(ApplicationId applicationId,
comment|//        List<ResourceRequest> ask, List<Container> release) throws IOException {
comment|//      Container container = recordFactory.newRecordInstance(Container.class);
comment|//      container.setContainerToken(recordFactory.newRecordInstance(ContainerToken.class));
comment|//      container.setNodeId(recordFactory.newRecordInstance(NodeId.class));
comment|//      container.setContainerManagerAddress("localhost");
comment|//      container.setNodeHttpAddress("localhost:8042");
comment|//      container.setId(recordFactory.newRecordInstance(ContainerId.class));
comment|//      container.getId().setAppId(appID);
comment|//      container.getId().setId(count);
comment|//      count++;
comment|//      return new Allocation(Arrays.asList(container), Resources.none());
comment|//    }
comment|//
comment|//    @Override
comment|//    public void handle(ASMEvent<ApplicationTrackerEventType> event) {
comment|//      switch (event.getType()) {
comment|//      case ADD:
comment|//        schedulerAddApplication++;
comment|//        break;
comment|//      case EXPIRE:
comment|//        schedulerRemoveApplication++;
comment|//        LOG.info("REMOVING app : " + schedulerRemoveApplication);
comment|//        if (schedulerRemoveApplication == maxFailures) {
comment|//          synchronized (schedulerNotify) {
comment|//            schedulerNotify.addAndGet(1);
comment|//            schedulerNotify.notify();
comment|//          }
comment|//        }
comment|//        break;
comment|//      default:
comment|//        break;
comment|//      }
comment|//    }
comment|//
comment|//    @Override
comment|//    public QueueInfo getQueueInfo(String queueName,
comment|//        boolean includeChildQueues,
comment|//        boolean recursive) throws IOException {
comment|//      return null;
comment|//    }
comment|//    @Override
comment|//    public List<QueueUserACLInfo> getQueueUserAclInfo() {
comment|//      return null;
comment|//    }
comment|//    @Override
comment|//    public void addApplication(ApplicationId applicationId,
comment|//        ApplicationMaster master, String user, String queue, Priority priority,
comment|//        ApplicationStore store)
comment|//        throws IOException {
comment|//    }
comment|//    @Override
comment|//    public void addNode(RMNode nodeInfo) {
comment|//    }
comment|//    @Override
comment|//    public void recover(RMState state) throws Exception {
comment|//    }
comment|//    @Override
comment|//    public void reinitialize(Configuration conf,
comment|//        ContainerTokenSecretManager secretManager, RMContext rmContext)
comment|//        throws IOException {
comment|//    }
comment|//
comment|//    @Override
comment|//    public void nodeUpdate(RMNode nodeInfo,
comment|//        Map<String, List<Container>> containers) {
comment|//    }
comment|//
comment|//    @Override
comment|//    public Resource getMaximumResourceCapability() {
comment|//      // TODO Auto-generated method stub
comment|//      return null;
comment|//    }
comment|//
comment|//    @Override
comment|//    public Resource getMinimumResourceCapability() {
comment|//      // TODO Auto-generated method stub
comment|//      return null;
comment|//    }
comment|//  }
comment|//
comment|//  @Before
comment|//  public void setUp() {
comment|//
comment|//    asmContext.getDispatcher().register(ApplicationEventType.class,
comment|//        new ResourceManager.ApplicationEventDispatcher(asmContext));
comment|//
comment|//    appID = recordFactory.newRecordInstance(ApplicationId.class);
comment|//    appID.setClusterTimestamp(System.currentTimeMillis());
comment|//    appID.setId(1);
comment|//    Configuration conf = new Configuration();
comment|//    scheduler = new DummyResourceScheduler();
comment|//    asmContext.getDispatcher().init(conf);
comment|//    asmContext.getDispatcher().start();
comment|//    asmContext.getDispatcher().register(ApplicationTrackerEventType.class, scheduler);
comment|//    appImpl = new ExtApplicationsManagerImpl(appTokenSecretManager, scheduler, asmContext);
comment|//
comment|//    conf.setLong(YarnConfiguration.AM_EXPIRY_INTERVAL, 1000L);
comment|//    conf.setInt(RMConfig.AM_MAX_RETRIES, maxFailures);
comment|//    appImpl.init(conf);
comment|//    appImpl.start();
comment|//
comment|//    this.clientRMService = new ClientRMService(asmContext, appImpl
comment|//        .getAmLivelinessMonitor(), appImpl.getClientToAMSecretManager(),
comment|//        scheduler);
comment|//    this.clientRMService.init(conf);
comment|//  }
comment|//
comment|//  @After
comment|//  public void tearDown() {
comment|//  }
comment|//
comment|//  private void waitForFailed(AppAttempt application, ApplicationState
comment|//      finalState) throws Exception {
comment|//    int count = 0;
comment|//    while(application.getState() != finalState&& count< 10) {
comment|//      Thread.sleep(500);
comment|//      count++;
comment|//    }
comment|//    Assert.assertEquals(finalState, application.getState());
comment|//  }
comment|//
comment|//  @Test
comment|//  public void testAMRestart() throws Exception {
comment|//    ApplicationSubmissionContext subContext = recordFactory.newRecordInstance(ApplicationSubmissionContext.class);
comment|//    subContext.setApplicationId(appID);
comment|//    subContext.setApplicationName("dummyApp");
comment|////    subContext.command = new ArrayList<String>();
comment|////    subContext.environment = new HashMap<String, String>();
comment|////    subContext.fsTokens = new ArrayList<String>();
comment|//    subContext.setFsTokensTodo(ByteBuffer.wrap(new byte[0]));
comment|//    SubmitApplicationRequest request = recordFactory
comment|//        .newRecordInstance(SubmitApplicationRequest.class);
comment|//    request.setApplicationSubmissionContext(subContext);
comment|//    clientRMService.submitApplication(request);
comment|//    AppAttempt application = asmContext.getApplications().get(appID);
comment|//    synchronized (schedulerNotify) {
comment|//      while(schedulerNotify.get() == 0) {
comment|//        schedulerNotify.wait();
comment|//      }
comment|//    }
comment|//    Assert.assertEquals(maxFailures, launcherCleanupCalled);
comment|//    Assert.assertEquals(maxFailures, launcherLaunchCalled);
comment|//    Assert.assertEquals(maxFailures, schedulerAddApplication);
comment|//    Assert.assertEquals(maxFailures, schedulerRemoveApplication);
comment|//    Assert.assertEquals(maxFailures, application.getFailedCount());
comment|//    waitForFailed(application, ApplicationState.FAILED);
comment|//    stop = true;
comment|//  }
block|}
end_class

end_unit

